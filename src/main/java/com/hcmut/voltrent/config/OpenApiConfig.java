package com.hcmut.voltrent.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI vehicleServiceOpenAPI() {
                return new OpenAPI()
                                .info(new Info()
                                                .title("Vehicle Service API")
                                                .description("API documentation for Vehicle Service application")
                                                .version("1.0.0")
                                                .contact(new Contact()
                                                                .name("Voltrent Support")
                                                                .email("support@voltrent.com")
                                                                .url("https://voltrent.com"))
                                                .license(new License()
                                                                .name("Apache 2.0")
                                                                .url("http://springdoc.org")));
        }
}