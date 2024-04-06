package org.example.user;

import org.example.common.exceptions.GlobalExceptionHandler;
import org.example.common.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Import;

@ConfigurationPropertiesScan("org.example.user")
@SpringBootApplication
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
public class UserServer {
    public static void main(String[] args) {
        SpringApplication.run(UserServer.class, args);
    }
}