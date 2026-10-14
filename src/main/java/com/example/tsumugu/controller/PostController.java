package com.example.tsumugu.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.exception.BadRequestException;
import com.example.tsumugu.exception.ForbiddenException;
import com.example.tsumugu.exception.ResourceNotFoundException;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.service.CurrentUserService;
import com.example.tsumugu.service.PostAccessService;
import com.example.tsumugu.service.PostResponseService;
import com.example.tsumugu.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final CurrentUserService currentUserService;
    private final PostResponseService postResponseService;
    private final PostAccessService postAccessService;

    public PostController(PostService postService, PostRepository postRepository, CurrentUserService currentUserService, PostResponseService postResponseService, PostAccessService postAccessService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.currentUserService = currentUserService;
        this.postResponseService = postResponseService;
        this.postAccessService = postAccessService;
    }

    @PostMapping("/today")
    public PostResponse createOrUpdateTodayPost(@Valid @RequestBody PostRequest request) {
        Post post = postService.createOrUpdateTodayPost(
                request.getDiaryText(),
                request.getMood(),
                request.getSleepHours(),
                request.getIsPublic()
        );
        return postResponseService.toResponse(post);
    }
    
    @GetMapping("/today")
    public ResponseEntity<PostResponse> getTodayPost() {
        return postService.getTodayPost()
        		.map(postResponseService::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
    
    // 投稿の公開設定を変更する(投稿の持ち主のみ)
    @PatchMapping("/{postId}/visibility")
    public ResponseEntity<PostResponse> updateVisibility(
            @PathVariable Long postId,
            @RequestBody VisibilityRequest request) {

        if (request.getIsPublic() == null) {
            throw new BadRequestException("isPublicを指定してください");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("投稿が見つかりません"));

        if (!post.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new ForbiddenException("自分の投稿のみ変更できます");
        }

        Post updated = postService.updateVisibility(post, request.getIsPublic());
        return ResponseEntity.ok(postResponseService.toResponse(updated));
    }
    
 // 投稿の詳細を取得する(閲覧できる人のみ)
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("投稿が見つかりません"));

        if (!postAccessService.canView(post, currentUserService.getCurrentUser())) {
            throw new ForbiddenException("この投稿は閲覧できません");
        }

        return ResponseEntity.ok(postResponseService.toResponse(post));
    }
}