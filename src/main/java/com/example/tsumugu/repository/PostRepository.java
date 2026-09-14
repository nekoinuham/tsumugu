package com.example.tsumugu.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUserAndDate(User user, LocalDate date);

}