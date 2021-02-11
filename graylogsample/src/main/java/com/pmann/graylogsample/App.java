package com.pmann.graylogsample;

import java.io.IOException;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
/**
 * Hello world!
 *
 */
public class App 
{
	private static final Logger LOGGER = LogManager.getLogger(App.class);

	public static void main( String[] args )
    {
		LOGGER.info("Starting Graylog sample app");
        
        //String json = "{ \"f1\":\"Hello\",\"f2\":{\"f3\":\"World\"}}";
        //JsonElement jsonTree = JsonParser.parseString(json);      
        //System.out.println(jsonTree.toString());
        
        try {
        	HttpGelfClient httpGelfClient = HttpGelfClientBuilder.build(LOGGER, "http://192.168.122.219:12201/gelf");
        	sendPost(httpGelfClient);
        } catch (IOException e) {
            e.printStackTrace();
        }

		LOGGER.info("Complete");
    }
    
    private static void sendPost(HttpGelfClient httpGelfClient) throws IOException {
        StringBuilder gelfMsgBuilder = new StringBuilder();
        gelfMsgBuilder.append("{");
        gelfMsgBuilder.append("\"version\":\"1.1\",");
        gelfMsgBuilder.append("\"short_message\":\"short_message\",");
        gelfMsgBuilder.append("\"host\":\"host\"");
        gelfMsgBuilder.append("}");

        httpGelfClient.sendGelf(gelfMsgBuilder.toString());
    }
}
