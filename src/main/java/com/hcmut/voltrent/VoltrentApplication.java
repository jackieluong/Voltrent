package com.hcmut.voltrent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VoltrentApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoltrentApplication.class, args);
	}

}
