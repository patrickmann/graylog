package com.pmann.graylogsample;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Class to iterate through a series of JSON log messages and transform them to GELF format.
 * Input may be provided as either a file or an InputStream.
 * 
 * Assumptions: 
 * - Messages are separated by newline
 * - Each message is a valid JSON object
 * - There is no nesting of elements - the object contains only primitive attributes
 * - Messages do not contain any reserved GELF field names, e.g. "version"
 * 
 * @author pmann
 *
 */
public class GelfGenerator implements AutoCloseable {
	private static final Logger LOGGER = LogManager.getLogger(GelfGenerator.class);
	public static final String GELF_PREFIX = "_";
	public static final String GELF_VERSION = "version";
	public static final String GELF_VERSION_VALUE = "1.1";
	public static final String GELF_HOST = "host";
	public static final String GELF_SHORT_MESSAGE = "short_message";

	private String hostName;
	private int counter = 0;
	private Scanner sc;

	/**
	 * Return a new instance to process the given InputStream
	 * @param inStream InputStream of JSON log messages
	 * @param hostName Host name that will be included in all GELF messages
	 */
	public GelfGenerator(InputStream inStream, String hostName) {
		this.hostName = hostName;
		sc = new Scanner(inStream);
	}

	/**
	 * Return a new instance to process the given file
	 * @param fileName path to a file of JSON log messages 
	 * @param hostName Host name that will be included in all GELF messages
	 * @throws FileNotFoundException thrown if file does not exist
	 */
	public GelfGenerator(String fileName, String hostName) throws FileNotFoundException {
		this.hostName = hostName;
		sc = new Scanner(new File(fileName));
	}

	/**
	 * Check for remaining input
	 * @return true, if another line is available for processing
	 */
	public boolean hasNext() {
		return sc.hasNextLine();
	}
	
	/**
	 * Process a single line and return the corresponding GELF log message
	 * @return GELF log message as String
	 * @throws IOException thrown if there was no input available for processing
	 */
	public String next() throws IOException {
		if (sc.hasNextLine()) {
			JsonElement jsonMsg = JsonParser.parseString(sc.nextLine());
			JsonObject objIn = jsonMsg.getAsJsonObject();
			JsonObject objOut = new JsonObject();
			
			Set<Entry<String, JsonElement>> entries = objIn.entrySet();
			for (Entry<String, JsonElement> entry : entries) {
				objOut.addProperty(GELF_PREFIX + entry.getKey(), entry.getValue().getAsString());
			}
			
			addRequiredFields(objOut, "message " + ++counter);
			LOGGER.debug(objOut.toString());
			return objOut.toString();	
		}
		else {
			throw new IOException("Called next() after last message");
		}
	}

	/**
	 * Implements AutoCloseable method
	 */
	@Override
	public void close() {
		LOGGER.info("Processed {} messages", counter);
		if (sc != null) {
			sc.close();
		}
	}

	/**
	 * Enrich the given JSON object with the mandatory GELF fields
	 * @param obj a JSON object
	 * @param shortMsg the value of the GELF short_message field for this message 
	 */
	private void addRequiredFields(JsonObject obj, String shortMsg) {
		obj.addProperty(GELF_VERSION, GELF_VERSION_VALUE);
		obj.addProperty(GELF_HOST, hostName);
		obj.addProperty(GELF_SHORT_MESSAGE, shortMsg);
	}
}
