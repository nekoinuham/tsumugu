package com.example.tsumugu.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {
	
	private final UserService userService;
	
	private final AuthenticationManager authenticationManager;
	
	private final SecurityContextRepository securityContextRepository =
			new HttpSessionSecurityContextRepository();
	
	public UserController(UserService userService, AuthenticationManager authenticationManager) {
	    this.userService = userService;
	    this.authenticationManager = authenticationManager;
	}
	
	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
		UserResponse response = userService.register(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
	    Authentication authentication = authenticationManager.authenticate(
	            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
	    );

	    SecurityContextHolder.getContext().setAuthentication(authentication);
	    securityContextRepository.saveContext(SecurityContextHolder.getContext(), httpRequest, null);

	    return ResponseEntity.ok("ログインしました");
	}
}