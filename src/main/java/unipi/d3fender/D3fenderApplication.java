package unipi.d3fender;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import unipi.d3fender.models.User;
import unipi.d3fender.repositories.UserRepository;

@SpringBootApplication
public class D3fenderApplication {

	public static void main(String[] args) {
		SpringApplication.run(D3fenderApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeAdminUser(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			@Value("${d3fender.admin.username}") String adminUsername,
			@Value("${d3fender.admin.email}") String adminEmail,
			@Value("${d3fender.admin.password}") String adminPassword
	) {
		return args -> {
			boolean adminExists = userRepository.findByUsername(adminUsername).isPresent();

			if (adminExists) {
				return;
			}

			User admin = new User();
			admin.setUsername(adminUsername);
			admin.setEmail(adminEmail);
			admin.setPassword(passwordEncoder.encode(adminPassword));


			admin.setSubscriptionPlan("PRO");

			userRepository.save(admin);
		};
	}
}
