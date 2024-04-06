package org.example.message;

import org.example.common.exceptions.GlobalExceptionHandler;
import org.example.common.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@ConfigurationPropertiesScan("org.example.message")
@SpringBootApplication
@EnableFeignClients(basePackages = {"org.example"})
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
public class MessageServer {
    public static void main(String[] args) {
        SpringApplication.run(MessageServer.class, args);
    }
}