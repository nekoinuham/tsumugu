package com.example.tsumugu.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.tsumugu.controller.RegisterRequest;
import com.example.tsumugu.controller.UserResponse;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.UserRepository;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public UserResponse register(RegisterRequest request) {
		if (userRepository.findByEmail(request.getEmail()).isPresent()) {
			throw new IllegalArgumentException("このメールアドレスは既に登録されています");
		}
		
		User user = new User();
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setDisplayName(request.getDisplayName());
		
		LocalDateTime now = LocalDateTime.now();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		
		User saved = userRepository.save(user);
		
		UserResponse response = new UserResponse();
		response.setId(saved.getId());
		response.setEmail(saved.getEmail());
		response.setDisplayName(saved.getDisplayName());
		
		return response;
	}
}