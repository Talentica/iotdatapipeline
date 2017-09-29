package com.talentica.iot;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by CalvinI on 27-09-2017.
 */

@Configuration
public class MongoConfiguration {

    @Bean
    public Mongo mongo(@Value("${mongodb.url}") String url) throws Exception {
        return new MongoClient(url);
    }

    @Bean
    public MongoTemplate mongoTemplate(Mongo mongo, @Value("${mongodb.schema}") String schema) throws Exception {
        return new MongoTemplate(mongo, schema);
    }
}
