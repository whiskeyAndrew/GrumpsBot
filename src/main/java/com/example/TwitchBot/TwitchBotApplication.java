package com.example.TwitchBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class TwitchBotApplication {	public static void main(String[] args) {
		SpringApplication.run(TwitchBotApplication.class, args);
	System.out.println();
	}

}
