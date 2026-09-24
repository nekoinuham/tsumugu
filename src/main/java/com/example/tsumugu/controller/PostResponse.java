package com.example.tsumugu.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.tsumugu.entity.Post;

import lombok.Getter;

@Getter
public class PostResponse {

    private final Long id;
    private final Long userId;
    private final String userDisplayName;
    private final LocalDate date;
    private final String diaryText;
    private final Integer mood;
    private final Double sleepHours;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public PostResponse(Post post) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.userDisplayName = post.getUser().getDisplayName();
        this.date = post.getDate();
        this.diaryText = post.getDiaryText();
        this.mood = post.getMood();
        this.sleepHours = post.getSleepHours();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
    }
}