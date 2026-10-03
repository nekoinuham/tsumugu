package com.example.tsumugu.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CurrentUserService currentUserService;

    public PostService(PostRepository postRepository, CurrentUserService currentUserService) {
        this.postRepository = postRepository;
        this.currentUserService = currentUserService;
    }
    
    public Post createOrUpdateTodayPost(String diaryText, Integer mood, Double sleepHours) {
    	User currentUser = currentUserService.getCurrentUser();
    	LocalDate today = LocalDate.now();
    	
    	Post post = postRepository.findByUserAndDate(currentUser, today)
    			.orElse(new Post());
    	
    	post.setUser(currentUser);
    	post.setDate(today);
    	post.setDiaryText(diaryText);
    	post.setMood(mood);
    	post.setSleepHours(sleepHours);
    	
    	LocalDateTime now = LocalDateTime.now();
    	if (post.getId() == null) {
    		post.setCreatedAt(now);
    	}
    	post.setUpdatedAt(now);
    	
    	return postRepository.save(post);
    }
    
    public Optional<Post> getTodayPost() {
        User currentUser = currentUserService.getCurrentUser();
        LocalDate today = LocalDate.now();
        return postRepository.findByUserAndDate(currentUser, today);
    }
}