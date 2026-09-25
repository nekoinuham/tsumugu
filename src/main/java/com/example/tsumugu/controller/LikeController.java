package com.example.tsumugu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.service.LikeService;

@RestController
@RequestMapping("/api/posts")
public class LikeController {
	
	private final LikeService likeService;
	private final PostRepository postRepository;
	
	public LikeController(LikeService likeService, PostRepository postRepository) {
		this.likeService = likeService;
		this.postRepository = postRepository;
	}
	
	@PostMapping("/{postId}/likes")
	public ResponseEntity<Long> likePost(@PathVariable Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));
		
		if (!post.isPublic()) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		
		long likeCount = likeService.likePost(post);
		return ResponseEntity.ok(likeCount);
	}
	
	@DeleteMapping("/{postId}/likes")
	public ResponseEntity<Long> unlikePost(@PathVariable Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));
		
		long likeCount = likeService.unlikePost(post);
		return ResponseEntity.ok(likeCount);
	}
}
