package com.example.smartpay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SmartPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartPayApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(UserRepository repository) {
		return (args) -> {
			repository.save(new User("Rahul", 5000.0));
			repository.save(new User("Divya", 2500.0));

			System.out.println("\n--- USERS FOUND IN DATABASE ---");
			for (User user : repository.findAll()) {
				System.out.println(user.toString());
			}
			System.out.println("-------------------------------\n");
		};
	}
}