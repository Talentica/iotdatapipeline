package org.apache.ignite.iot;


import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class PahoDemo {

  MqttClient client;
  
  public PahoDemo() {}

  public static void main(String[] args) {
    new PahoDemo().doDemo();
  }

  public void doDemo() {
    try {
      client = new MqttClient("tcp://172.19.103.71:1883", "pahomqttpublish1");
      MqttConnectOptions options = new MqttConnectOptions();
      options.setCleanSession(false);
      options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
      options.setConnectionTimeout(10);
      options.setKeepAliveInterval(300);
      
      
      
      int i = 4;
      while (i < 8){
	      MqttMessage message = new MqttMessage();
	      int device_id = i;
	      int temperature = 30+ i;
	      JSONObject obj = new JSONObject();
	      try {
			obj.put("device_id", device_id);
			obj.put("temperature", temperature);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	      
	      
	      message.setPayload(obj.toString().getBytes());
	      //options.setWill("sensorTopic2", message.getPayload(), 1, false);
	      client.connect(options);
	      client.publish("sensorTopic2", message.getPayload(), 1, false);
	      client.disconnect();
	      i = i+ 1;
	  }
      
    } catch (MqttException e) {
      e.printStackTrace();
    }
  }
}