package com.example.tsumugu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.service.PostResponseService;
import com.example.tsumugu.service.UserProfileService;

@RestController
@RequestMapping("/api/users")
public class UserProfileController {

    private final UserProfileService userProfileService;
    private final PostResponseService postResponseService;

    public UserProfileController(UserProfileService userProfileService, PostResponseService postResponseService) {
        this.userProfileService = userProfileService;
        this.postResponseService = postResponseService;
    }

    // プロフィールを取得する
    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(userProfileService.getProfile(userId));
    }

    // そのユーザーの投稿一覧を取得する(beforeIdで続き)
    @GetMapping("/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getPosts(
            @PathVariable Long userId,
            @RequestParam(required = false) Long beforeId) {

        List<Post> posts = userProfileService.getPosts(userId, beforeId);
        return ResponseEntity.ok(postResponseService.toResponses(posts));
    }
}