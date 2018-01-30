package com.talentica.iot.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

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

	/**
	 * Timestamp of the record.
	 */
	private Date ts; // This should come from the sensor (ms UTC). generating
						// this on server can cause analytic-error due to lag.

	public Temperature() {
	}

	public Temperature(Integer deviceId, Float temperature) {
		this.deviceId = deviceId;
		this.temperature = temperature;
	}
	
	@Override
	public String toString() {
		return "Temperature [id=" + id + ", deviceId=" + deviceId + ", temperature=" + temperature + ", ts=" + ts + "]";
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

	public Date getTs() {
		return ts;
	}

	public void setTs(Date ts) {
		this.ts = ts;
	}
}