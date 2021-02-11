package com.pmann.graylogsample;

import org.apache.logging.log4j.Logger;

public class HttpGelfClientBuilder {
	// Class cannot be instantiated
	private HttpGelfClientBuilder() {}
	
	public static HttpGelfClient build(Logger logger, String uri) {
		return new HttpGelfClientImpl(logger, uri);
	}
}
