package com.pmann.graylogsample;

/**
 * Factory methods to create GELF clients
 * @author pmann
 *
 */
public class GelfClientBuilder {
	// Class only provides static methods - do not instantiate
	private GelfClientBuilder() {}
	
	public static GelfClient build(String uri) {
		return new HttpGelfClientImpl(uri);
	}
}
