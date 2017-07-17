package com.talentica.iot.mongo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.talentica.iot.domain.Temperature;

public interface TemperatureRepository extends MongoRepository<Temperature, Integer> {

	public Temperature findByDeviceId(Integer deviceId);

}
