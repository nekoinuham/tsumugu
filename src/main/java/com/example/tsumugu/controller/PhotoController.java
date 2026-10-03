package com.example.tsumugu.controller;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.tsumugu.entity.Photo;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.service.CurrentUserService;
import com.example.tsumugu.service.PhotoService;

@RestController
@RequestMapping("/api/posts")
public class PhotoController {

    private final PhotoService photoService;
    private final PostRepository postRepository;
    private final CurrentUserService currentUserService;

    public PhotoController(PhotoService photoService, PostRepository postRepository, CurrentUserService currentUserService) {
        this.photoService = photoService;
        this.postRepository = postRepository;
        this.currentUserService = currentUserService;
    }

    // 写真を追加する(投稿の持ち主のみ)
    @PostMapping("/{postId}/photos")
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "comment", required = false) String comment) throws IOException {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("投稿が見つかりません"));

        // 他人の投稿には写真を追加できない
        if (!post.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Photo photo = photoService.uploadPhoto(post, file, comment);
        return ResponseEntity.ok(new PhotoResponse(photo));
    }
}