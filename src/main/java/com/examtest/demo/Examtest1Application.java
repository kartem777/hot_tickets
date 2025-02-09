package com.examtest.demo;

import com.examtest.demo.dto.user.UserRegistrationDto;
import com.examtest.demo.exception.RegistrationException;
import com.examtest.demo.model.User;
import com.examtest.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Examtest1Application {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(Examtest1Application.class, args);

	}
	@PostConstruct
	public void initializeAdmin() {
		if (userRepository.count() == 0) {
			String email = "admin@example.com";
			String password = "adminPass";

			UserRegistrationDto userDto = new UserRegistrationDto(email, password, password);

			try {
				User adminUser = registerAdmin(userDto);
				System.out.println("Admin user created: " + adminUser.getEmail());
			} catch (RegistrationException e) {
				System.err.println("Error creating admin user: " + e.getMessage());
			}
		}
	}

	private User registerAdmin(UserRegistrationDto userDto) throws RegistrationException {
		if (userRepository.existsByEmail(userDto.email())) {
			throw new RegistrationException("Email is already registered");
		}

		User admin = new User();
		admin.setEmail(userDto.email());
		admin.setPassword(passwordEncoder.encode(userDto.password()));
		admin.setRole(User.Role.ADMIN);

		return userRepository.save(admin);
	}
}
