package com.talentica.iot;

import com.mongodb.MongoClient;
import com.talentica.iot.domain.TemperatureMongo;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by CalvinI on 26-09-2017.
 */
public class MongoDbHelper {

    private static MongoDbHelper instance = null;

    private Datastore morphia = null;

    private MongoDbHelper() {

    }

    public static synchronized MongoDbHelper getInstance() {
        if(instance == null) {
            instance = new MongoDbHelper();
        }
        return instance;
    }

    public static void initializeConnection(String host,String schema) {
        instance.createConnection(host,schema);
    }

    private void createConnection(String host,String schema) {
        MongoClient mongo = new MongoClient(host);


        Set<Class> clss = new HashSet<>();

        Collections.addAll(clss, TemperatureMongo.class);

        morphia = new Morphia(clss).createDatastore(mongo, schema);

    }

    public Datastore getDatastore() {
        return morphia;
    }
}
