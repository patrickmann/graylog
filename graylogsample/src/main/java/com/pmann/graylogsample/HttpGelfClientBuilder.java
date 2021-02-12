package com.pmann.graylogsample;

public class HttpGelfClientBuilder {
	// Class only provides static methods - do not instantiate
	private HttpGelfClientBuilder() {}
	
	public static HttpGelfClient build(String uri) {
		return new HttpGelfClientImpl(uri);
	}
}
