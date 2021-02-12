package com.pmann.graylogsample;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.Properties;

import org.junit.jupiter.api.Test;

class AppTest 
{
	// Successfully loads some properties from file
    @Test
    void loadPropertiesSuccess() throws IOException
    {
    	Properties properties = App.loadProperties();
    	
        assertNotNull(properties, "Could not populate properties");
        assertFalse(properties.isEmpty(), "Properties initialized but 0 entries");
    }
}
