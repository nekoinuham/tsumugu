package com.example.tsumugu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Photo;
import com.example.tsumugu.entity.Post;

public interface PhotoRepository extends JpaRepository<Photo, Long> {

	List<Photo> findByPost(Post post);
	
}