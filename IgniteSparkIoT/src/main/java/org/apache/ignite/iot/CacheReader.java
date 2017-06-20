package org.apache.ignite.iot;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.ignite.iot.model.Sensor;
import org.apache.ignite.iot.model.TempKey;
import org.apache.ignite.spark.JavaIgniteContext;
import org.apache.ignite.spark.JavaIgniteRDD;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaStreamingContext;



public class CacheReader {
	
	private static JavaStreamingContext streamingCxt;
	private static JavaIgniteContext igniteCxt;
	private static JavaIgniteRDD<TempKey, Float> tempRdd;
	
	public static void main(String[] args) throws InterruptedException {
		// Create a local StreamingContext with two working thread and batch interval of 500 milliseconds.
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("IgniteSparkIoT");
        streamingCxt = new JavaStreamingContext(conf, Durations.milliseconds(1000));
     // Creates Ignite context and connects to it as a client application.
        igniteCxt = new JavaIgniteContext(
            streamingCxt.sparkContext(), "config/ignite-config.xml", true);
        
        // Getting a reference to temperature cache via the shared RDD.
        tempRdd = igniteCxt.<Integer, Sensor>fromCache("TemperatureCache");
        
     // Scheduling the timer to execute queries over the cluster.
        new Timer().schedule(new TimerTask() {
            @Override public void run() {
                System.out.println(" >>> Samples count:" + tempRdd.count());

                Dataset ds = tempRdd.sql("SELECT sensorId, count(*) From Float WHERE temp > 70 and temp < 100" +
                    " GROUP BY sensorId ORDER BY sensorID");

                ds.show();
            }
        }, 1000, 4000);
        
        
	}

}

