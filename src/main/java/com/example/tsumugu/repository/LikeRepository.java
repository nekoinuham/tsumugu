package com.example.tsumugu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Like;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;

public interface LikeRepository extends JpaRepository<Like, Long> {
	
	Optional<Like> findByPostAndUser(Post post, User user);
	
	Long countByPost(Post post);
}