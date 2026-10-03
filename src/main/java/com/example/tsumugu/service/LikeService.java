package com.example.tsumugu.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Like;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.LikeRepository;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final CurrentUserService currentUserService;

    public LikeService(LikeRepository likeRepository, CurrentUserService currentUserService) {
        this.likeRepository = likeRepository;
        this.currentUserService = currentUserService;
    }

    public long likePost(Post post) {
        User currentUser = currentUserService.getCurrentUser();

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
        User currentUser = currentUserService.getCurrentUser();

        likeRepository.findByPostAndUser(post, currentUser)
                .ifPresent(likeRepository::delete);

        return likeRepository.countByPost(post);
    }
}