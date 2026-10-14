package com.example.tsumugu.controller;

import java.time.LocalDateTime;

import com.example.tsumugu.entity.User;

import lombok.Getter;

/**
 * ログイン中の自分の情報のレスポンス。
 * パスワードは含めない。メールアドレスは本人にだけ返す。
 */
@Getter
public class MeResponse {

    private final Long id;
    private final String email;
    private final String displayName;
    private final Boolean isPrivate;
    private final LocalDateTime createdAt;

    public MeResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.displayName = user.getDisplayName();
        this.isPrivate = user.isPrivate();
        this.createdAt = user.getCreatedAt();
    }
}