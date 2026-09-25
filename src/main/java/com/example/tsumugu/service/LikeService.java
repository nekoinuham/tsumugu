package com.example.tsumugu.service;

import java.time.LocalDateTime;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Like;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.LikeRepository;
import com.example.tsumugu.security.CustomUserDetails;

@Service
public class LikeService {

    private final LikeRepository likeRepository;

    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    private User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return userDetails.getUser();
    }

    public long likePost(Post post) {
        User currentUser = getCurrentUser();

        boolean alreadyLiked = likeRepository.findByPostAndUser(post, currentUser).isPresent();
        if (!alreadyLiked) {
            Like like = new Like();
            like.setPost(post);
            like.setUser(currentUser);
            like.setCreatedAt(LocalDateTime.now());
            likeRepository.save(like);
        }

        return likeRepository.countByPost(post);
    }

    public long unlikePost(Post post) {
        User currentUser = getCurrentUser();

        likeRepository.findByPostAndUser(post, currentUser)
                .ifPresent(likeRepository::delete);

        return likeRepository.countByPost(post);
    }
}