package com.example.tsumugu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.service.CurrentUserService;
import com.example.tsumugu.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final CurrentUserService currentUserService;

    public PostController(PostService postService, PostRepository postRepository, CurrentUserService currentUserService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/today")
    public PostResponse createOrUpdateTodayPost(@RequestBody PostRequest request) {
        Post post = postService.createOrUpdateTodayPost(
                request.getDiaryText(),
                request.getMood(),
                request.getSleepHours(),
                request.getIsPublic()
        );
        return new PostResponse(post);
    }
    
    @GetMapping("/today")
    public ResponseEntity<PostResponse> getTodayPost() {
        return postService.getTodayPost()
        		.map(PostResponse::new)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    
    // 投稿の公開設定を変更する(投稿の持ち主のみ)
    @PatchMapping("/{postId}/visibility")
    public ResponseEntity<PostResponse> updateVisibility(
            @PathVariable Long postId,
            @RequestBody VisibilityRequest request) {

        if (request.getIsPublic() == null) {
            return ResponseEntity.badRequest().build();
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));

        if (!post.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Post updated = postService.updateVisibility(post, request.getIsPublic());
        return ResponseEntity.ok(new PostResponse(updated));
    }
}