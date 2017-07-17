package com.talentica.iot.mqtt.client.impl;

import java.io.Serializable;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.ignite.spark.JavaIgniteContext;
import org.apache.ignite.spark.JavaIgniteRDD;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
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
import org.springframework.stereotype.Component;

import com.talentica.iot.domain.TempKey;

import scala.Tuple2;

@Component("sparkIgniteStreamer")
public class SparkIgniteStreamer extends SparkStreamerImpl implements Serializable {

	private static final Logger logger = Logger.getLogger(SparkIgniteStreamer.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String streamerName = "IgniteStreamer";

	public void run() {
		logger.info("streamerName: " + streamerName + " brokerUrl: " + brokerUrl + " sparkBatchInterval: "
				+ sparkBatchInterval + " topic: " + topic);

		IgniteServerNodeStartup.run();

		JavaStreamingContext context = initializeContext();
		logger.info("Spark Streamer Context is up and running.");
		JavaIgniteContext<TempKey, Float> igniteContext = new JavaIgniteContext<TempKey, Float>(context.sparkContext(),
				"ignite-config.xml", true);

		logger.info("Spark Streamer is up and running.");

		// Getting a reference to temperature cache via the shared RDD.
		JavaIgniteRDD<TempKey, Float> igniteRdd = igniteContext.fromCache("TemperatureCache");

		saveStreamDataToIgniteCache(context, igniteRdd);

	}

	private JavaStreamingContext initializeContext() {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName(streamerName);
		return new JavaStreamingContext(conf, Durations.milliseconds(sparkBatchInterval));
	}

	private void saveStreamDataToIgniteCache(JavaStreamingContext context,
			final JavaIgniteRDD<TempKey, Float> igniteRdd) {
		JavaReceiverInputDStream<String> rawData = MQTTUtils.createStream(context, brokerUrl, topic,
				StorageLevel.MEMORY_AND_DISK(), clientId, null, null, false, 1, 10, 300,
				MqttConnectOptions.MQTT_VERSION_3_1_1);

		// Split each sample into a tuple that contains 'sensorId' and
		// `temperature` data.
		JavaPairDStream<Integer, Float> samples = rawData.mapToPair(new PairFunction<String, Integer, Float>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Tuple2<Integer, Float> call(String s) throws Exception {
				JSONObject strJson = new JSONObject(s);
				Integer device_id = Integer.parseInt(strJson.getString("device_id"));
				Float temperature = Float.parseFloat(strJson.getString("temperature"));
				logger.info("Mapping1 device_id: " + device_id + " temperature: " + temperature);
				return new Tuple2<Integer, Float>(device_id, temperature);
			}
		});

		// Transform the sample to Ignite cache entry pairs.
		JavaPairDStream<TempKey, Float> igniteEntries = samples
				.mapToPair(new PairFunction<Tuple2<Integer, Float>, TempKey, Float>() {
					private static final long serialVersionUID = 1L;

					@Override
					public Tuple2<TempKey, Float> call(Tuple2<Integer, Float> tuple2) throws Exception {
						System.out.println("Mapping2 device_id: " + tuple2._1() + " temperature: " + tuple2._2());
						return new Tuple2<TempKey, Float>(new TempKey(tuple2._1(), new Date()), tuple2._2());
					}
				});

		// Push data to Apache Ignite cluster.
		igniteEntries.foreachRDD(new VoidFunction<JavaPairRDD<TempKey, Float>>() {
			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaPairRDD<TempKey, Float> rdd) throws Exception {
				if (rdd != null) {
					logger.info("Saving to Ignite");
					igniteRdd.savePairs(rdd);
				}

			}
		});

		context.start();

		logger.info("Streaming of sensors' samples is activated.");

		// Scheduling the timer to execute queries over the cluster.
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				logger.info("Samples count:" + igniteRdd.count());

				Dataset<Row> ds = igniteRdd.sql("SELECT count(*) From Float ");

				ds.show();

			}
		}, 1000, 4000);

		try {
			context.awaitTermination();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
