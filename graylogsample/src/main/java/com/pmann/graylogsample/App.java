package com.pmann.graylogsample;

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
    }
}
