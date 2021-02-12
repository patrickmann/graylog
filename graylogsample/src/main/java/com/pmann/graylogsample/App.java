package com.pmann.graylogsample;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {
	private static final Logger LOGGER = LogManager.getLogger(App.class);

	public static void main(String[] args) {
		LOGGER.info("Starting Graylog sample app");
		
		Properties properties = loadProperties(); 
		try (GelfGenerator generator = new GelfGenerator(properties.getProperty("filename"), properties.getProperty("hostname"))) {
			
			HttpGelfClient httpGelfClient = HttpGelfClientBuilder.build(properties.getProperty("server.uri"));
			while (generator.hasNext()) {
				httpGelfClient.sendGelf(generator.next());				
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

		LOGGER.info("Complete");
	}

	private static final String PROPFILE = "config.properties";
	private static Properties loadProperties() {
        Properties properties = new Properties();
	    try (InputStream input = App.class.getClassLoader().getResourceAsStream(PROPFILE)) {
	        if (input == null) {
	        	throw new IOException("Unable to find property file: " + PROPFILE);
	        }
	        else {
	        	properties.load(input);
	        }
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
		return properties;
	}
}

