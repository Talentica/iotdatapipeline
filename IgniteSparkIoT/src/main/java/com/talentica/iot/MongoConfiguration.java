package com.talentica.iot;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by CalvinI on 27-09-2017.
 */

@Configuration
@EnableMongoRepositories(basePackages = "com.talentica.iot.mongo.repository")
public class MongoConfiguration extends AbstractMongoConfiguration {

    @Value("${mongodb.url}") String url;
    @Value("${mongodb.schema}") String schema;


    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(url);
    }

    @Override
    protected String getDatabaseName() {
        return schema;
    }
}