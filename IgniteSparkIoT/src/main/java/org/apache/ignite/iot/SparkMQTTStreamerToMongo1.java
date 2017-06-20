package org.apache.ignite.iot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.ignite.iot.model.Sensor;
import org.apache.ignite.iot.model.TempKey;
import org.apache.ignite.spark.JavaIgniteContext;
import org.apache.ignite.spark.JavaIgniteRDD;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.receiver.Receiver;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONObject;

import com.google.common.base.Splitter;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.util.JSON;

import scala.Tuple2;

public class SparkMQTTStreamerToMongo1 extends BasicDBObject implements Serializable  {
	
	
	public static int SENSORS_CNT = 1000;

    /** */
    private static JavaStreamingContext streamingCxt;

    /** */
    private static JavaIgniteContext igniteCxt;

    /** */
    private static JavaIgniteRDD<Integer, Sensor> sensorsRdd;

    /** */
    private static JavaIgniteRDD<TempKey, Float> tempRdd;
    
    private static Mongo mongo = new Mongo("172.19.103.74", 27017);
 	private final static DB db = mongo.getDB("mydb");
 	private final static DBCollection collection = db.getCollection("temperature");
 	//private static List<DBObject> dbObjects ;
    /**
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        // Create a local StreamingContext with two working thread and batch interval of 500 milliseconds.
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("IgniteSparkIoT");

        // Spark context. Setting the window of 1 sec for streaming data
        streamingCxt = new JavaStreamingContext(conf, Durations.milliseconds(1000));

        // Adjust the logger to exclude the logs of no interest.
        Logger.getRootLogger().setLevel(Level.ERROR);
        Logger.getLogger("org.apache.ignite").setLevel(Level.INFO);
        

        // Creates Ignite context and connects to it as a client application.
        igniteCxt = new JavaIgniteContext(
            streamingCxt.sparkContext(), args[0], true);
        
        

        System.out.println(">>> Spark Streamer is up and running.");

        // Getting a reference to temperature cache via the shared RDD.
        tempRdd = igniteCxt.<Integer, Sensor>fromCache("TemperatureCache");
        

        System.out.println(">>> Shared RDDs are instantiated.");

        // Initiate streaming from IoT to Apache Ignite via Spark Streaming For CSV Datatype. 
        //streamSensorMeasurements();
        
        // Initiate streaming from IoT to Apache Ignite via Spark Streaming For JSON Datatype.
        //streamSensorMeasurementsJSON();
        
     // Initiate streaming from IoT to Apache Ignite via Spark Streaming For JSON Datatype and Dumping to Mongo
        streamSensorMeasurementsJSONToMongo();
    }

    private static void streamSensorMeasurementsJSONToMongo()   throws InterruptedException {
        // Create a DStream that will connect to MQTT broker with tcp protocol hostname:port
    	String brokerUrl = "tcp://172.19.103.71:1883";
     	String topic = "sensorTopic1";
     	String clientId = "client1"; 
     	//JavaReceiverInputDStream<String> rawData = MQTTUtils.createStream(streamingCxt, brokerUrl, topic);
     	JavaReceiverInputDStream<String> rawData = 
     			MQTTUtils.createStream(streamingCxt, brokerUrl, topic, StorageLevel.MEMORY_AND_DISK(), clientId, null, null, false, 1, 10, 300, MqttConnectOptions.MQTT_VERSION_3_1_1);
     	
    	
        // Split each sample into a tuple that contains 'sensorId' and `temperature` data.
        JavaPairDStream<Integer, Float> samples = rawData.mapToPair(new PairFunction<String, Integer, Float>() {
            @Override public Tuple2<Integer, Float> call(String s) throws Exception {
            	JSONObject strJson = new JSONObject(s);
            	DBObject dbObject = (DBObject) JSON.parse(s);
            	//dbObjects.add(dbObject);
            	collection.insert(dbObject);
                Integer device_id = Integer.parseInt(strJson.getString("device_id"));
				//String topic_id = obj.getString("topic");
				Float temperature = Float.parseFloat(strJson.getString("temperature"));
				
                return new Tuple2<Integer, Float>(device_id, temperature);
            }
        });
        
        
        			
        samples.count().print();
        
        /*// Transform the sample to Ignite cache entry pairs.
        JavaPairDStream<TempKey, Float> igniteEntries = samples.mapToPair(
            new PairFunction<Tuple2<Integer, Float>, TempKey, Float>() {
            @Override public Tuple2<TempKey, Float> call(Tuple2<Integer, Float> tuple2) throws Exception {
            	//System.out.println("Tuple2 for " + tuple2._1()+ " and " +tuple2._2());
                return new Tuple2<TempKey, Float>(new TempKey(tuple2._1(), new Date()), tuple2._2());
            }
        });*/


        // Performing additional required transformation according to the use case.
        // ....

        /*// Push data to Apache Ignite cluster.
        igniteEntries.foreachRDD(new VoidFunction<JavaPairRDD<TempKey, Float>>() {
            @Override public void call(JavaPairRDD<TempKey, Float> rdd) throws Exception {
            	//System.out.println("Tuple2 for " + rdd._1()+ " and " +rdd._2());
            	
                tempRdd.savePairs(rdd);
                
            }
        });*/

        streamingCxt.start();

        System.out.println(" >>> Streaming of sensors' samples is activated.");

        // Scheduling the timer to execute queries over the cluster.
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                System.out.println(" >>> Samples count:" + tempRdd.count());

                //Dataset ds = tempRdd.sql("SELECT sensorId, count(*) From Float " +
                    //" GROUP BY sensorId ORDER BY sensorID");
                
                Dataset ds = tempRdd.sql("SELECT count(*) From Float ");

                ds.show();
                /*Dataset dsMax = tempRdd.sql("SELECT sensorId , max(temp) as max, min(temp) as min, "
                		+ " count(*) as count From Float group by sensorId ");
                dsMax.show();*/
                
            }
        }, 1000, 4000);
        
        

        streamingCxt.awaitTermination();
    }
}
