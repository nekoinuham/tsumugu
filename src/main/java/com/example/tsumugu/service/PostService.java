package com.example.tsumugu.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.repository.UserRepository;
import com.example.tsumugu.security.CustomUserDetails;

@Service
public class PostService {

    // 認証機能ができるまでの固定ユーザーID
    private static final Long CURRENT_USER_ID = 1L;

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser();
    }
    
    public Post createOrUpdateTodayPost(String diaryText, Integer mood, Double sleepHours) {
    	User currentUser = getCurrentUser();
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
        User currentUser = getCurrentUser();
        LocalDate today = LocalDate.now();
        return postRepository.findByUserAndDate(currentUser, today);
    }
}