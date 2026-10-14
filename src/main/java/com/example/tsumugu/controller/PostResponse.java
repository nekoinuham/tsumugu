package com.example.tsumugu.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.tsumugu.entity.Photo;
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
    private final Boolean isPublic;
    private final List<PhotoResponse> photos;
    private final long likeCount;
    private final boolean likedByMe;
    private final long commentCount;

    public PostResponse(Post post, List<Photo> photos, long likeCount, boolean likedByMe, long commentCount) {
        this.id = post.getId();
        this.userId = post.getUser().getId();
        this.userDisplayName = post.getUser().getDisplayName();
        this.date = post.getDate();
        this.diaryText = post.getDiaryText();
        this.mood = post.getMood();
        this.sleepHours = post.getSleepHours();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.isPublic = post.isPublic();
        this.photos = photos.stream().map(PhotoResponse::new).toList();
        this.likeCount = likeCount;
        this.likedByMe = likedByMe;
        this.commentCount = commentCount;
    }
}