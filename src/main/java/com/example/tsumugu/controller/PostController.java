package com.example.tsumugu.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.service.PostService;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/today")
    public Post createOrUpdateTodayPost(@RequestBody PostRequest request) {
        return postService.createOrUpdateTodayPost(
                request.getDiaryText(),
                request.getMood(),
                request.getSleepHours()
        );
    }
}