package com.example.tsumugu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Post;

public interface PostRepository extends JpaRepository<Post, Long> {

}