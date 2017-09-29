package com.talentica.iot;

import com.talentica.iot.mqtt.client.ISparkStreamer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@Import(value = {MongoConfiguration.class})
@EnableMongoRepositories(basePackages = "com.talentica.iot.mongo.repository")
public class SparkStreamerApplication implements CommandLineRunner {
	
	@Autowired
	@Qualifier("sparkMongoDBStreamer")
	ISparkStreamer sparkMongoDBStreamer;
	
	@Autowired
	@Qualifier("sparkIgniteStreamer")
	ISparkStreamer sparkIgniteStreamer;

    @Value("${mongodb.url}")
    protected String mongodbUrl;

    @Value("${mongodb.schema}")
    protected String mongodbSchema;

	public static void main(String[] args) {
		SpringApplication.run(SparkStreamerApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
        
		String argument = null;
		if (args.length > 0) {
			argument = args[0];
		}
		if (argument == null || argument.equalsIgnoreCase("help")
				|| (!argument.equalsIgnoreCase("mongo") && !argument.equalsIgnoreCase("ignite"))) {
			printHelp();
		}

		
		ISparkStreamer streamer = createStreamer(argument);
		streamer.startStreamer();
		
	}
	

	private ISparkStreamer createStreamer(String streamerType) {

		if (streamerType.equals("mongo")) {
			return sparkMongoDBStreamer;
		} else if (streamerType.equals("ignite")) {
            MongoDbHelper.getInstance();
            MongoDbHelper.initializeConnection(mongodbUrl,mongodbSchema);
			return sparkIgniteStreamer;
		}

		return sparkMongoDBStreamer;
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
