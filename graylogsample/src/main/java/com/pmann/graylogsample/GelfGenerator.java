package com.pmann.graylogsample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GelfGenerator implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(GelfGenerator.class);
	public static final String GELF_PREFIX = "_";
	public static final String GELF_VERSION = "version";
	public static final String GELF_VERSION_VALUE = "1.1";
	public static final String GELF_HOST = "host";
	public static final String GELF_SHORT_MESSAGE = "short_message";

	private String hostName;
	private int counter = 1;
	private Scanner sc;

	public GelfGenerator(String fileName, String hostName) throws FileNotFoundException {
		this.hostName = hostName;
		sc = new Scanner(new File(fileName));
	}

	public boolean hasNext() {
		return sc.hasNextLine();
	}
	
	public String next() throws IOException {
		if (sc.hasNextLine()) {
			JsonElement jsonMsg = JsonParser.parseString(sc.nextLine());
			JsonObject objIn = jsonMsg.getAsJsonObject();
			JsonObject objOut = new JsonObject();
			
			Set<Entry<String, JsonElement>> entries = objIn.entrySet();
			for (Entry<String, JsonElement> entry : entries) {
				objOut.addProperty(GELF_PREFIX + entry.getKey(), entry.getValue().getAsString());
			}
			
			addRequiredFields(objOut, "message " + counter++);
			LOGGER.debug(objOut.toString());
			return objOut.toString();	
		}
		else {
			throw new IOException("Called next() after last message");
		}
	}

	@Override
	public void close() {
		if (sc != null) {
			sc.close();
		}
	}

	private void addRequiredFields(JsonObject obj, String shortMsg) {
		obj.addProperty(GELF_VERSION, GELF_VERSION_VALUE);
		obj.addProperty(GELF_HOST, hostName);
		obj.addProperty(GELF_SHORT_MESSAGE, shortMsg);
	}
}
