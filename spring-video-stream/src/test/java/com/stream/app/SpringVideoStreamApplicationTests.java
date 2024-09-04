package com.stream.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.stream.app.services.VideoService;

@SpringBootTest
class SpringVideoStreamApplicationTests {
	
	@Autowired
	VideoService videoService;

	@Test
	void contextLoads() {
		
		videoService.processVideo("4e78c6e0-1b4b-4bec-bfb8-4b24a38ed1aa");
	}

}
