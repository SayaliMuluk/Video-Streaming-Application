package com.stream.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stream.app.entities.Course;
import com.stream.app.services.VideoService;

@SpringBootApplication
public class SpringVideoStreamApplication {
	

	public static void main(String[] args) {
		
		
		SpringApplication.run(SpringVideoStreamApplication.class, args);
		
	}

}
