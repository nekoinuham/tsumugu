package com.example.tsumugu.controller;

import com.example.tsumugu.entity.User;
import com.example.tsumugu.service.FollowStatus;

import lombok.Getter;

/**
 * ユーザーのプロフィールのレスポンス(他人にも見せる情報だけ)。
 */
@Getter
public class UserProfileResponse {

    private final Long id;
    private final String displayName;
    private final Boolean isPrivate;
    private final Boolean isMe;
    private final FollowStatus followStatus;
    private final Boolean canViewPosts;

    public UserProfileResponse(User user, boolean isMe, FollowStatus followStatus, boolean canViewPosts) {
        this.id = user.getId();
        this.displayName = user.getDisplayName();
        this.isPrivate = user.isPrivate();
        this.isMe = isMe;
        this.followStatus = followStatus;
        this.canViewPosts = canViewPosts;
    }
}