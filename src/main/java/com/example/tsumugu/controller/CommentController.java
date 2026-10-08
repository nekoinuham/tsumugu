package com.example.tsumugu.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.entity.Comment;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.service.CommentService;
import com.example.tsumugu.service.CurrentUserService;
import com.example.tsumugu.service.PostAccessService;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    private final CommentService commentService;
    private final PostRepository postRepository;
    private final PostAccessService postAccessService;
    private final CurrentUserService currentUserService;

    public CommentController(
            CommentService commentService,
            PostRepository postRepository,
            PostAccessService postAccessService,
            CurrentUserService currentUserService) {
        this.commentService = commentService;
        this.postRepository = postRepository;
        this.postAccessService = postAccessService;
        this.currentUserService = currentUserService;
    }

    // コメントを投稿する
    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));

        if (!postAccessService.canView(post, currentUserService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Comment comment = commentService.addComment(post, request.getCommentText());
        return ResponseEntity.ok(new CommentResponse(comment));
    }

    // 投稿に紐づくコメント一覧を取得する
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));

        if (!postAccessService.canView(post, currentUserService.getCurrentUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<CommentResponse> responses = commentService.getComments(post)
                .stream()
                .map(CommentResponse::new)
                .toList();

        return ResponseEntity.ok(responses);
    }

    // コメントを削除する(投稿者本人のみ)
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}