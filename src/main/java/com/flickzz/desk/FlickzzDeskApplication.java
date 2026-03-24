package com.flickzz.desk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.flickzz.desk.model") 
@EnableJpaRepositories(basePackages = "com.flickzz.desk.repo")
public class FlickzzDeskApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlickzzDeskApplication.class, args);
	}

}
