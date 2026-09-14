package com.example.tsumugu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
}
