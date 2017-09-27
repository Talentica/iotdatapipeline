package com.talentica.iot.cachestore;

import com.talentica.iot.MongoDbHelper;
import com.talentica.iot.domain.TempKey;
import com.talentica.iot.domain.TemperatureMongo;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@Component("mongoCacheStore")
public class MongoCacheStore  extends CacheStoreAdapter<TempKey, TemperatureMongo> implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(MongoCacheStore.class);
	

	public MongoCacheStore() {

	}

    @Override public TemperatureMongo load(TempKey key) throws CacheLoaderException {
    	TemperatureMongo temp = MongoDbHelper.getInstance().getDatastore().find(TemperatureMongo.class).field("id").equal(key).get();

        logger.info("Loaded temperature: " + temp);

        return temp;
    }

    @Override public void write(Cache.Entry<? extends TempKey, ? extends TemperatureMongo> cacheEntry) throws CacheWriterException {
    	TemperatureMongo temp = cacheEntry.getValue();

        MongoDbHelper.getInstance().getDatastore().save(cacheEntry.getValue());

        logger.info("Stored temperature: " + cacheEntry.getValue());
    }

    /** {@inheritDoc} */
    @Override public void delete(Object key) throws CacheWriterException {
    	TemperatureMongo temp = MongoDbHelper.getInstance().getDatastore().find(TemperatureMongo.class).field("id").equal(key).get();
    	
    	if(temp != null) {
            MongoDbHelper.getInstance().getDatastore().delete(temp);
            logger.info("Removed temperature: " + key);
    	}
    }
    
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        logger.info("Deserializing");
    }
    
    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        logger.info("Serializing");
    }
}