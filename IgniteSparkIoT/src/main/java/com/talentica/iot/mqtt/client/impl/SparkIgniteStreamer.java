package com.talentica.iot.mqtt.client.impl;

import java.io.Serializable;
import java.util.*;

import javax.cache.configuration.FactoryBuilder;

import com.talentica.iot.domain.Temperature;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.store.CacheStore;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.spark.IgniteContext;
import org.apache.ignite.spark.IgniteRDD;
import org.apache.ignite.spark.JavaIgniteContext;
import org.apache.ignite.spark.JavaIgniteRDD;
import org.apache.log4j.Logger;
import org.apache.spark.Partition;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.TaskContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.talentica.iot.cachestore.MongoCacheStore;
import com.talentica.iot.domain.TempKey;
import com.talentica.iot.domain.TemperatureMongo;

import scala.Tuple2;
import scala.collection.Iterator;

@Component("sparkIgniteStreamer")
public class SparkIgniteStreamer extends SparkStreamerImpl implements Serializable {

	private static final Logger logger = Logger.getLogger(SparkIgniteStreamer.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String streamerName = "IgniteStreamer";
	
	
	@Autowired
	MongoCacheStore mongoCacheStore;

	public void run() {
		logger.info("streamerName: " + streamerName + " brokerUrl: " + brokerUrl + " sparkBatchInterval: "
				+ sparkBatchInterval + " topic: " + topic);

		IgniteServerNodeStartup.run();

        JavaStreamingContext context = initializeContext();
        logger.info("Spark Streamer Context is up and running.");
        JavaIgniteContext<TempKey, TemperatureMongo> igniteContext = new JavaIgniteContext<TempKey, TemperatureMongo>(context.sparkContext(),
                "ignite-mongo.xml", true);

		logger.info("Spark Streamer is up and running.");

		// Getting a reference to temperature cache via the shared RDD.
        IgniteCache<TempKey,TemperatureMongo> igniteCache = igniteContext.ignite().getOrCreateCache("TemperatureCache");
		JavaIgniteRDD<TempKey,TemperatureMongo> igniteRDD = igniteContext.fromCache("TemperatureCache");

        saveStreamDataToIgniteCache(context,igniteCache,igniteRDD);

	}

	private JavaStreamingContext initializeContext() {


		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName(streamerName);
		return new JavaStreamingContext(conf, Durations.milliseconds(sparkBatchInterval));
	}

    private void saveStreamDataToIgniteCache(JavaStreamingContext context, IgniteCache<TempKey,TemperatureMongo> igniteCache,
                                             JavaIgniteRDD<TempKey,TemperatureMongo> igniteRDD) {
        JavaReceiverInputDStream<String> rawData = MQTTUtils.createStream(context, brokerUrl, topic,
                StorageLevel.MEMORY_AND_DISK(), clientId, null, null, false, 1, 10, 300,
                MqttConnectOptions.MQTT_VERSION_3_1_1);

        rawData.foreachRDD(new VoidFunction<JavaRDD<String>>() {

            @Override
            public void call(JavaRDD<String> stringJavaRDD) throws Exception {
                if (stringJavaRDD != null) {
                    List<String> collect = stringJavaRDD.collect();
                    Map<TempKey,TemperatureMongo> temperatureMongoMap = new HashMap();
                    for (String data : collect) {
                        JSONObject strJson = new JSONObject(data);
                        Integer deviceId = Integer.parseInt(strJson.getString("device_id"));
                        Float temperature = Float.parseFloat(strJson.getString("temperature"));
                        TempKey tempKey = new TempKey();
                        tempKey.setSensorId(deviceId);
                        tempKey.setTs(new Date());
                        TemperatureMongo temperatureMongo = new TemperatureMongo(tempKey,temperature);
                        temperatureMongoMap.put(temperatureMongo.getId(),temperatureMongo);
                    }
                    igniteCache.putAll(temperatureMongoMap);
                }
            }
        });

		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Samples count:" + igniteRDD.count());

				Dataset<Row> ds = igniteRDD.sql("SELECT count(*) From TemperatureMongo ");

				ds.show();

			}
		}, 1000, 4000);
        context.start();
        try {
            context.awaitTermination();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
