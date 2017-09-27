package com.talentica.iot.mongo.repository;

import com.talentica.iot.domain.Temperature;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by CalvinI on 27-09-2017.
 */

@Repository
public interface TemperatureRepository extends MongoRepository<Temperature,String> {
}
