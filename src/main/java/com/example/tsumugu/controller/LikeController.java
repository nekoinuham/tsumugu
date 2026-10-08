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
import com.example.tsumugu.service.CurrentUserService;
import com.example.tsumugu.service.LikeService;
import com.example.tsumugu.service.PostAccessService;

@RestController
@RequestMapping("/api/posts")
public class LikeController {

    private final LikeService likeService;
    private final PostRepository postRepository;
    private final PostAccessService postAccessService;
    private final CurrentUserService currentUserService;

    public LikeController(LikeService likeService, PostRepository postRepository,
            PostAccessService postAccessService, CurrentUserService currentUserService) {
        this.likeService = likeService;
        this.postRepository = postRepository;
        this.postAccessService = postAccessService;
        this.currentUserService = currentUserService;
    }

    // いいねする(投稿を見られる人のみ)
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Long> likePost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));

        if (!postAccessService.canView(post, currentUserService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        long likeCount = likeService.likePost(post);
        return ResponseEntity.ok(likeCount);
    }

    // いいねを取り消す
    @DeleteMapping("/{postId}/likes")
    public ResponseEntity<Long> unlikePost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));

        long likeCount = likeService.unlikePost(post);
        return ResponseEntity.ok(likeCount);
    }
}