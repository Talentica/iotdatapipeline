package com.talentica.iot.mqtt.client.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.storage.StorageLevel;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.mqtt.MQTTUtils;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.talentica.iot.domain.Temperature;
import com.talentica.iot.mongo.repository.TemperatureRepository;

@Component("sparkMongoDBStreamer")
public class SparkMongoDBStreamer extends SparkStreamerImpl {

	@Autowired
	private TemperatureRepository temperatureRepository;

	private static final String streamerName = "MongoStreamer";

	public void run() {
		System.out.println("streamerName: " + streamerName + " brokerUrl: " + brokerUrl + " sparkBatchInterval: "
				+ sparkBatchInterval + " topic: " + topic + " mongodbUrl: " + mongodbUrl);

		JavaStreamingContext context = initializeContext();
		saveStreamDataToMongo(context);

	}

	private JavaStreamingContext initializeContext() {
		SparkConf conf = new SparkConf().setMaster("local[2]").setAppName(streamerName);
		return new JavaStreamingContext(conf, Durations.milliseconds(sparkBatchInterval));
	}

	private void saveStreamDataToMongo(JavaStreamingContext context) {
		JavaReceiverInputDStream<String> rawData = MQTTUtils.createStream(context, brokerUrl, topic,
				StorageLevel.MEMORY_AND_DISK(), clientId, null, null, false, 1, 10, 300,
				MqttConnectOptions.MQTT_VERSION_3_1_1);

		rawData.foreachRDD(new VoidFunction<JavaRDD<String>>() {

			private static final long serialVersionUID = 1L;

			@Override
			public void call(JavaRDD<String> rdd) throws Exception {
				if (rdd != null) {
					List<String> collect = rdd.collect();
					List<Temperature> temperatureList = new ArrayList<>();
					for (String data : collect) {
						JSONObject strJson = new JSONObject(data);
						Temperature sampleTemperature = new Temperature();
						Integer deviceId = Integer.parseInt(strJson.getString("device_id"));
						Float temperature = Float.parseFloat(strJson.getString("temperature"));
						sampleTemperature.setDeviceId(deviceId);
						sampleTemperature.setTemperature(temperature);
						temperatureList.add(sampleTemperature);
						//temperatureRepository.save(sampleTemperature);
					}
					temperatureRepository.save(temperatureList);
				}
			}
		});

		context.start();
		try {
			context.awaitTermination();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
