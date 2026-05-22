package com.gps.mx.laviga.dev3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LaVigaDev3Application {

    public static void main(String[] args) {
    	// Forzar carga del properties
	    System.setProperty("spring.config.location", "classpath:/application.properties");
        SpringApplication.run(LaVigaDev3Application.class, args);
    }
}
