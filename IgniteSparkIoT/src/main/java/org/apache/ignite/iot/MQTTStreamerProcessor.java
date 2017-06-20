package org.apache.ignite.iot;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.internal.util.typedef.F;
import org.apache.ignite.Ignition;
import org.apache.ignite.binary.BinaryObject;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.iot.model.TempKey;
import org.apache.ignite.lang.IgniteInClosure;
import org.apache.ignite.stream.StreamMultipleTupleExtractor;
import org.apache.ignite.stream.mqtt.MqttStreamer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.base.Splitter;



public class MQTTStreamerProcessor {
	
	
	
	public static void main(String[] args) throws Exception {
		Ignition.setClientMode(true);
		String brokerUrl = "tcp://172.19.103.71:1883";
		Ignition.start("config/ignite-config.xml");
		Ignite ignite = Ignition.ignite("grid1");
		//IgniteCache<TempKey, Float> stmCache = ignite.getOrCreateCache("TemperatureCache");
		IgniteCache<TempKey, Float> stmCache = ignite.cache("TemperatureCache");
		//CacheConfiguration<TempKey, Float> cfg = stmCache.getConfiguration(CacheConfiguration.class);
		Logger.getRootLogger().setLevel(Level.ERROR);
        Logger.getLogger("org.apache.ignite").setLevel(Level.INFO);
        IgniteDataStreamer<TempKey, Float> stmr = ignite.dataStreamer(stmCache.getName());
        
        if (ignite.cluster().forDataNodes(stmCache.getName()).nodes().isEmpty()) {
            System.out.println();
            System.out.println(">>> This example requires remote cache node nodes to be started.");
            System.out.println(">>> Please start at least 1 remote cache node.");
            System.out.println(">>> Refer to example's javadoc for details on configuration.");
            System.out.println();

            return;
        }
        
        MqttStreamer<TempKey, Float> streamer = new MqttStreamer<TempKey, Float>();
		stmr.allowOverwrite(true);
		streamer.setIgnite(ignite);
		streamer.setStreamer(stmr);
		streamer.setBrokerUrl(brokerUrl);
		
		streamer.setTopics(Arrays.asList("sensorTopic"));
		streamer.setBlockUntilConnected(true);
		
		
		streamer.setMultipleTupleExtractor(new StreamMultipleTupleExtractor<MqttMessage, TempKey, Float>() {
		    @Override public Map<TempKey, Float> extract(MqttMessage msg) {
		        List<String> s = Splitter.on("|").splitToList(new String(msg.getPayload()));
		        System.out.println("message "+ s.get(0) + " and " + s.get(1));
		        Map<TempKey, Float> entries = new HashMap<TempKey, Float>();
		    	String jsonString = new String(msg.getPayload());
		    	System.out.println("Json "+ jsonString);
		    	JSONObject obj = null;
		    	List<JSONObject> objs = null;
		    	final Map<TempKey, Float> answer = new HashMap<>();
		    	
		    	String id_key = null;
		    	
				try {
					
					obj = new JSONObject(s.get(1));
					Integer device_id = Integer.parseInt(obj.getString("device_id"));
					String topic_id = obj.getString("topic");
					Float temperature = Float.parseFloat(obj.getString("temperature"));
					Long evt_timestamp = Long.parseLong(s.get(0).replace("ts_sep_flag", ""));
					Long evt_timestamp_processed = GetProcessedTimeStamp(evt_timestamp);
    		    	//System.out.println("evt_timestamp_processed "+ evt_timestamp_processed);
					id_key = device_id + "_" + topic_id + "_" + evt_timestamp.toString();
					String id_groupBykey = device_id + "_" + evt_timestamp_processed.toString();
					obj.put("id_key", id_key);
					obj.put("id_groupBykey", id_groupBykey);
					obj.put("evnt_timestamp", evt_timestamp_processed);
					//objs.add(obj);
					//dbObjects.add(dbObject);
					System.out.println("obj "+ obj.toString());
					TempKey sensorObj = new TempKey(device_id, new Date());
					entries.put(sensorObj, temperature);
					
					

		                

		                F.forEach(entries.keySet(), new IgniteInClosure<TempKey>() {
		                    @Override public void apply(TempKey s) {
		                        answer.put(s, entries.get(s));
		                    }
		                });
		                
		                
					//keyForQuery.add(id_key);
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    	
		    	
				/*try {
					
					//
					//String timeStamp = 
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				
		        
		        //collection.insert(dbObjects);
				//stmCache.put(id_key, obj); 
		        //System.out.println("Loaded in Mongo");
		        //return new GridMapEntry<>(Integer.parseInt(s.get(0)), s.get(1));
		        //stmCache.putAll(entries);
		                   		        
				return answer;
		    	
		    }
		});
		
		// Start the MQTT Streamer.
		System.out.println("Streamer Started");
		streamer.start();
		stmr.close();     		
		
        
	}

	
    
	public static long GetProcessedTimeStamp(long timeStampinEpoch) {
		int timeStartInterval = 30000;
		int timeEndInterval = 29999;
		
		//long now1 = Instant.now().toEpochMilli();
		long start = System.currentTimeMillis();
		long end = timeStampinEpoch/timeStartInterval * timeStartInterval;
		long end1 = end + timeEndInterval;
		//System.out.println(timeStampinEpoch);
		//System.out.println(end);
		//System.out.println(end1);
		return end;

	};
	
	

}
