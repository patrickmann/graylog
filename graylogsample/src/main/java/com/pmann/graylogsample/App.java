package com.pmann.graylogsample;

import java.io.IOException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println("Hello World!");
        
        String json = "{ \"f1\":\"Hello\",\"f2\":{\"f3\":\"World\"}}";
        JsonElement jsonTree = JsonParser.parseString(json);
        
        System.out.println(jsonTree.toString());
        
        
        try {
            String result = sendPOST("http://192.168.122.219:12201/gelf");
            System.out.println(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String sendPOST(String url) throws IOException {

        String result = "";
        HttpPost post = new HttpPost(url);

        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"version\":\"1.1\",");
        json.append("\"short_message\":\"short_message\",");
        json.append("\"host\":\"host\"");
        json.append("}");

        // send a JSON data
        post.setEntity(new StringEntity(json.toString()));

        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(post)){

        	System.out.println(response.getStatusLine());
            result = EntityUtils.toString(response.getEntity());
        }

        return result;
    }
}
