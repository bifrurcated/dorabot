package com.flowixlab.dorabot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

@SpringBootApplication
public class DoraBotApplication {

	public static void main(String[] args) {
		Hooks.onOperatorDebug();
		SpringApplication.run(DoraBotApplication.class, args);
	}

}
