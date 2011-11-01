package de.ronnyfriedland.time.config;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;

public class SystemPropertiesTest {

    @Test
    public void testSystemProperties() {
        Properties props = System.getProperties();

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }

}
