package com.example.tsumugu.controller;

import java.time.LocalDateTime;

import com.example.tsumugu.entity.Comment;

import lombok.Getter;

@Getter
public class CommentResponse {
	
	private final Long id;
	private final Long userId;
	private final String userDisplayName;
	private final String commentText;
	private final LocalDateTime createdAt;
	
	public CommentResponse(Comment comment) {
		this.id = comment.getId();
		this.userId = comment.getUser().getId();
		this.userDisplayName = comment.getUser().getDisplayName();
		this.commentText = comment.getCommentText();
		this.createdAt = comment.getCreatedAt();
	}
}
