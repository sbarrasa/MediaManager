package com.blink.mediaserver;

import java.util.Properties;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;


@SpringBootApplication
public class MediaServer {

    public static void main(String[] args) {
		new SpringApplicationBuilder(MediaServer.class)
			.properties(staticProperties())
			.run(args);
	}
	
	
	private static Properties staticProperties() {
		Properties properties = new Properties();
		properties.put("spring.application.name","MediaServer");
		properties.put("spring.application.version","1.0.1");
		return properties;
	}
}
