package com.talentica.iot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
public class Temperature implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public String id;

	public Integer deviceId;
	public Float temperature;

	public Temperature() {
	}

	public Temperature(Integer deviceId, Float temperature) {
		this.deviceId = deviceId;
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		return String.format("Temperature[id=%s, deviceId='%d', temperature='%f']", id, deviceId, temperature);
	}

	public void setDeviceId(Integer deviceId) {
		this.deviceId = deviceId;
	}

	public void setTemperature(Float temperature) {
		this.temperature = temperature;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getDeviceId() {
		return deviceId;
	}

	public Float getTemperature() {
		return temperature;
	}

	public String getId() {
		return id;
	}

}