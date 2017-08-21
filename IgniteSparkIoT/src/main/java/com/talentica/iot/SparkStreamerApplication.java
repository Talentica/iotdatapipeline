package com.talentica.iot;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.talentica.iot.mqtt.client.impl.SparkStreamerImpl;

public class SparkStreamerApplication {

	public static void main(String[] args) {
		String argument = null;
		if (args.length > 0) {
			argument = args[0];
		}
		if (argument == null || argument.equalsIgnoreCase("help")
				|| (!argument.equalsIgnoreCase("mongo") && !argument.equalsIgnoreCase("ignite"))) {
			printHelp();
		}

		try (ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
				"application-context.xml")) {
			SparkStreamerImpl streamer = createStreamer(args[0], applicationContext);
			streamer.startStreamer();
		}
	}

	public static SparkStreamerImpl createStreamer(String streamerType,
			ClassPathXmlApplicationContext applicationContext) {

		if (streamerType.equals("mongo")) {
			return (SparkStreamerImpl) applicationContext.getBean("sparkMongoDBStreamer");
		} else if (streamerType.equals("ignite")) {
			return (SparkStreamerImpl) applicationContext.getBean("sparkIgniteStreamer");
		}

		return (SparkStreamerImpl) applicationContext.getBean("sparkMongoDBStreamer");
	}

	public static void printHelp() {
		System.out.println();
		System.out.println(
				"Usage: java -cp <jar-file> <options> com.talentica.iot.SparkStreamerApplication mongo/ignite");
		System.out.println();
		System.out.println(
				"Available options: -Dbroker.url, -Dspark.interval, -Dmongodb.url, -Dmongodb.schema, -Dtopic, -Dclient.id, -Dtarget_env");
		System.out.println();
		System.out.println("\t-Dbroker.url,\t\tOptional,\tex: -Dbroker.url=tcp://localhost:1883");
		System.out.println("\t-Dspark.interval,\tOptional,\tex: -Dspark.interval=1000");
		System.out.println("\t-Dmongodb.url,\t\tOptional,\tex: -Dmongodb.url=localhost");
		System.out.println("\t-Dmongodb.schema,\tOptional,\tex: -Dmongodb.schema=test");
		System.out.println("\t-Dtopic,\t\tOptional,\tex: -Dtopic=temperature");
		System.out.println("\t-Dclient.id,\t\tOptional,\tex: -Dclient.id=clientId1");
		System.out.println("\t-Dtarget_env,\t\tOptional,\tex: -Dtarget_env=development/staging/production");
		System.out.println();
		System.exit(1);
	}

}
