package com.talentica.iot.mqtt.client.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.talentica.iot.mqtt.client.ISparkStreamer;

@Component
public abstract class SparkStreamerImpl implements ISparkStreamer {

	@Value("${broker.url}")
	protected String brokerUrl;

	@Value("${spark.interval}")
	protected Integer sparkBatchInterval;

	@Value("${topic}")
	protected String topic;

	@Value("${client.id}")
	protected String clientId;
	
	@Value("${mongodb.url}")
	protected String mongodbUrl;
	
	@Value("${mongodb.schema}")
	protected String mongodbSchema;

	@Override
	public void startStreamer() {
		this.run();
	}

	public abstract void run();

}
