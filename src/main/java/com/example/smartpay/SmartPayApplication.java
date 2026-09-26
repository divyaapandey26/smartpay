package com.example.smartpay;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class SmartPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmartPayApplication.class, args);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CommandLineRunner demo(UserRepository userRepository,
	                              UPIAppRepository appRepository,
	                              OfferRepository offerRepository,
	                              PasswordEncoder passwordEncoder) {
		return (args) -> {
			// Seed users with hashed passwords
			if (userRepository.count() == 0) {
				userRepository.save(new User("Shweta", "shweta",
						passwordEncoder.encode("shweta123"), 5000.0, "ROLE_USER"));
				userRepository.save(new User("Divya", "divya",
						passwordEncoder.encode("divya123"), 2500.0, "ROLE_ADMIN"));
			}

			// Seed UPI apps
			if (appRepository.count() == 0) {
				appRepository.save(new UPIApp("GPay", "Google", true));
				appRepository.save(new UPIApp("PhonePe", "Walmart", true));
				appRepository.save(new UPIApp("Paytm", "One97", true));
				appRepository.save(new UPIApp("BHIM", "NPCI", true));
			}

			// Seed offers
			if (offerRepository.count() == 0) {
				LocalDate today = LocalDate.now();
				LocalDate nextMonth = today.plusDays(30);

				offerRepository.save(new Offer(1L, "BigBasket", 5.0, 50.0, 500.0, today, nextMonth, true));
				offerRepository.save(new Offer(1L, "Swiggy", 10.0, 75.0, 200.0, today, nextMonth, true));
				offerRepository.save(new Offer(1L, "Amazon", 3.0, 100.0, 1000.0, today, nextMonth, true));

				offerRepository.save(new Offer(2L, "BigBasket", 8.0, 80.0, 500.0, today, nextMonth, true));
				offerRepository.save(new Offer(2L, "Swiggy", 5.0, 50.0, 300.0, today, nextMonth, true));
				offerRepository.save(new Offer(2L, "Flipkart", 6.0, 90.0, 800.0, today, nextMonth, true));

				offerRepository.save(new Offer(3L, "BigBasket", 3.0, 30.0, 300.0, today, nextMonth, true));
				offerRepository.save(new Offer(3L, "Amazon", 7.0, 150.0, 1500.0, today, nextMonth, true));
				offerRepository.save(new Offer(3L, "Zomato", 12.0, 100.0, 400.0, today, nextMonth, true));
			}

			System.out.println("\n--- SEED DATA LOADED ---");
			System.out.println("Users: " + userRepository.count());
			System.out.println("UPI Apps: " + appRepository.count());
			System.out.println("Offers: " + offerRepository.count());
			System.out.println("------------------------\n");
		};
	}
}