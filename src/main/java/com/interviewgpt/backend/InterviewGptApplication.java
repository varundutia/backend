package com.interviewgpt.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InterviewGptApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterviewGptApplication.class, args);
		System.out.println("Started SpringBoot Application");
	}

}
