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
import com.example.tsumugu.service.PhotoService;
import com.example.tsumugu.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final PostRepository postRepository;
    private final CurrentUserService currentUserService;
    private final PhotoService photoService;

    public PostController(PostService postService, PostRepository postRepository, CurrentUserService currentUserService, PhotoService photoService) {
        this.postService = postService;
        this.postRepository = postRepository;
        this.currentUserService = currentUserService;
        this.photoService = photoService;
    }
    
    // 投稿に写真の一覧を付けて、レスポンスの形にする
    private PostResponse toResponse(Post post) {
    	return new PostResponse(post, photoService.getPhotos(post));
    }

    @PostMapping("/today")
    public PostResponse createOrUpdateTodayPost(@Valid @RequestBody PostRequest request) {
        Post post = postService.createOrUpdateTodayPost(
                request.getDiaryText(),
                request.getMood(),
                request.getSleepHours(),
                request.getIsPublic()
        );
        return toResponse(post);
    }
    
    @GetMapping("/today")
    public ResponseEntity<PostResponse> getTodayPost() {
        return postService.getTodayPost()
        		.map(this::toResponse)
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
        return ResponseEntity.ok(toResponse(updated));
    }
}