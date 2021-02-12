package com.pmann.graylogsample;

import java.io.IOException;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpGelfClientImpl implements HttpGelfClient{
	private static final Logger LOGGER = LogManager.getLogger(HttpGelfClientImpl.class);
	private String targetUri;
	
	public HttpGelfClientImpl(String uri) {
		this.targetUri = uri;
	}

	@Override
	public void sendGelf(String gelfMsg) throws IOException {
		LOGGER.debug(gelfMsg);
		
        HttpPost post = new HttpPost(targetUri);
        post.setEntity(new StringEntity(gelfMsg));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)){

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() != 202) {
            	LOGGER.error("unexpected status code {}", statusLine.getStatusCode());
                throw new HttpResponseException(
                        statusLine.getStatusCode(),
                        statusLine.getReasonPhrase());
            }
        }
	}
}
