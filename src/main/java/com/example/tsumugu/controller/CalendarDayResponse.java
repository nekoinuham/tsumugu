package com.example.tsumugu.controller;

import java.time.LocalDate;

import com.example.tsumugu.entity.Post;

import lombok.Getter;

/**
 * カレンダーで、記録がある日を表すレスポンス。
 */
@Getter
public class CalendarDayResponse {

    private final LocalDate date;
    private final Long postId;

    public CalendarDayResponse(Post post) {
        this.date = post.getDate();
        this.postId = post.getId();
    }
}