package com.talentica.iot.domain;

import java.io.Serializable;
import java.util.UUID;

import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("tempmongo")
public class TemperatureMongo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//@SerializedName("_id")
	@Id
	@Embedded
    public TempKey id;
	
	public float temperature;

	public TemperatureMongo(TempKey key,float temperature) {
		this.id = key;
		this.temperature = temperature;
	}

	public TempKey getId() {
		return id;
	}

	public void setId(TempKey id) {
		this.id = id;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	@Override
	public String toString() {
		return "TemperatureMongo [id=" + id + ", temperature=" + temperature + "]";
	}
	
		

}
