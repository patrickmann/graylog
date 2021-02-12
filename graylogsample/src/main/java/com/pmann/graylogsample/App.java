package com.pmann.graylogsample;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Hello world!
 *
 */
public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);
	private static String GELF_PREFIX = "_";
	private static String GELF_VERSION = "version";
	private static String GELF_VERSION_VALUE = "1.1";
	private static String GELF_HOST = "host";
	private static String GELF_HOST_VALUE = "GraylogSample";
	private static String GELF_SHORT_MESSAGE = "short_message";

	public static void main(String[] args) {
		LOGGER.info("Starting Graylog sample app");

		JsonObject objOut = new JsonObject();

		try (FileInputStream fis = new FileInputStream("test-messages.txt"); Scanner sc = new Scanner(fis)) {
			while (sc.hasNextLine()) {
				JsonElement jsonMsg = JsonParser.parseString(sc.nextLine());
				JsonObject objIn = jsonMsg.getAsJsonObject();
				
				Set<Entry<String, JsonElement>> entries = objIn.entrySet();
				for (Entry<String, JsonElement> entry : entries) {
					objOut.addProperty(GELF_PREFIX + entry.getKey(), entry.getValue().getAsString());
				}
				
				addRequiredFields(objOut, "shortmsg");
				System.out.println(objOut.toString());
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		// String json = "{ \"f1\":\"Hello\",\"f2\":{\"f3\":\"World\"}}";
		// JsonElement jsonTree = JsonParser.parseString(json);
		// System.out.println(jsonTree.toString());

		try {
			HttpGelfClient httpGelfClient = HttpGelfClientBuilder.build(LOGGER, "http://192.168.122.219:12201/gelf");
			httpGelfClient.sendGelf(objOut.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOGGER.info("Complete");
	}

	private static void addRequiredFields(JsonObject obj, String shortMsg) {
		obj.addProperty(GELF_VERSION, GELF_VERSION_VALUE);
		obj.addProperty(GELF_HOST, GELF_HOST_VALUE);
		obj.addProperty(GELF_SHORT_MESSAGE, shortMsg);
	}
	
}
