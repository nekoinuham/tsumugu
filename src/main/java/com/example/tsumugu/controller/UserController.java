package com.example.tsumugu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
		UserResponse response = userService.register(request);
		return ResponseEntity.ok(response);
	}
}