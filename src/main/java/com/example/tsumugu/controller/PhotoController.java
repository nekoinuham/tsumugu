package com.example.tsumugu.controller;

import java.io.IOException;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tsumugu.entity.Photo;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.exception.ForbiddenException;
import com.example.tsumugu.exception.ResourceNotFoundException;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.service.CurrentUserService;
import com.example.tsumugu.service.PhotoService;

@RestController
@RequestMapping("/api")
public class PhotoController {

    private final PhotoService photoService;
    private final PostRepository postRepository;
    private final CurrentUserService currentUserService;

    public PhotoController(PhotoService photoService, PostRepository postRepository,
            CurrentUserService currentUserService) {
        this.photoService = photoService;
        this.postRepository = postRepository;
        this.currentUserService = currentUserService;
    }

    // 写真を追加する(投稿の持ち主のみ)
    @PostMapping("/posts/{postId}/photos")
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "comment", required = false) String comment) throws IOException {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("投稿が見つかりません"));

        // 他人の投稿には写真を追加できない
        if (!post.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new ForbiddenException("自分の投稿にのみ写真を追加できます");
        }

        Photo photo = photoService.uploadPhoto(post, file, comment);
        return ResponseEntity.ok(new PhotoResponse(photo));
    }

    // 写真のコメントを変更する(投稿の持ち主のみ)
    @PatchMapping("/photos/{photoId}")
    public ResponseEntity<PhotoResponse> updateComment(
            @PathVariable Long photoId,
            @Valid @RequestBody PhotoCommentRequest request) {

        Photo photo = photoService.updateComment(photoId, request.getComment());
        return ResponseEntity.ok(new PhotoResponse(photo));
    }
}