package com.talentica.iot.cachestore;

import java.io.Serializable;

import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;

import org.apache.ignite.IgniteLogger;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.resources.LoggerResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.talentica.iot.domain.Temperature;
import com.talentica.iot.mongo.repository.TemperatureIgniteRepository;

@Component("mongoCacheStore")
public class MongoCacheStore  extends CacheStoreAdapter<Integer, Temperature> implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@LoggerResource
    private IgniteLogger log;

   @Autowired
   TemperatureIgniteRepository temperatureIgniteRepository;

    @Override public Temperature load(Integer key) throws CacheLoaderException {
        Temperature temp = temperatureIgniteRepository.findByDeviceId(key);

        log("Loaded temperature: " + temp);

        return temp;
    }

    @Override public void write(Cache.Entry<? extends Integer, ? extends Temperature> cacheEntry) throws CacheWriterException {
    	temperatureIgniteRepository.save(cacheEntry.getValue());

        log("Stored temperature: " + cacheEntry.getValue());
    }

    /** {@inheritDoc} */
    @Override public void delete(Object key) throws CacheWriterException {
    	Temperature temp = temperatureIgniteRepository.findByDeviceId((Integer)key);
    	
    	if(temp != null) {
    		temperatureIgniteRepository.delete(temp);
    		log("Removed temperature: " + key);
    	}
    }

    /**
     * @param msg Message.
     */
    private void log(String msg) {
        if (log != null) {
            log.info(">>>");
            log.info(">>> " + msg);
            log.info(">>>");
        }
        else {
            System.out.println(">>>");
            System.out.println(">>> " + msg);
            System.out.println(">>>");
        }
    }
}