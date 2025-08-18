package com.in28minutes.rest.webservices.restfulwebservices.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private BCryptPasswordEncoder BCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.BCryptPasswordEncoder = BCryptPasswordEncoder;
    }

    public ResponseEntity<String> registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        // Set default role if none provided
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER"); // Default role
        }


        user.setPassword(BCryptPasswordEncoder.encode(user.getPassword())); // Encrypt password
        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully");
    }

    public List<User> getAllUsers() {
        return userRepository.findAll(); // Fetch all users from the database
    }
}
