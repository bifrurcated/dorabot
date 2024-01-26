package com.flowixlab.dorabot;

import com.flowixlab.dorabot.utils.ResourcesManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Hooks;

import java.io.IOException;

@SpringBootApplication
public class DoraBotApplication {

	public static void main(String[] args) throws IOException {
		ResourcesManager.initialize();
		Hooks.onOperatorDebug();
		SpringApplication.run(DoraBotApplication.class, args);
	}
}
