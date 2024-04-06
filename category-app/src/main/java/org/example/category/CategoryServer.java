package org.example.category;

import org.example.common.exceptions.GlobalExceptionHandler;
import org.example.common.security.SecurityConfig;
import org.example.common.security.props.SecurityProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

@ConfigurationPropertiesScan("org.example.category")
@SpringBootApplication
@EnableFeignClients(basePackages = {"org.example"})
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
public class CategoryServer {
    public static void main(String[] args) {
        SpringApplication.run(CategoryServer.class, args);
    }
}