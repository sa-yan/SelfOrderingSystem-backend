package com.sayan.selforderingsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.TimeZone;

@SpringBootApplication
@EnableAsync
public class SelfOrderingSystemApplication {

    public static void main(String[] args) {
        // Pin the JVM timezone to IST so order timestamps match India time,
        // not the host (Render runs in UTC). Set before Spring starts.
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        SpringApplication.run(SelfOrderingSystemApplication.class, args);
    }

}
