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
		
		Properties properties = null;
		try {
			properties = loadProperties();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			System.exit(1);
		}
		
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
	public static Properties loadProperties() throws IOException {
        Properties properties = new Properties();
	    try (InputStream input = App.class.getClassLoader().getResourceAsStream(PROPFILE)) {
	        if (input == null) {
	        	throw new IOException("Unable to find property file: " + PROPFILE);
	        }
	        else {
	        	properties.load(input);
	        }
		}
		return properties;
	}
}

