package com.example.tsumugu.controller;

import java.time.LocalDateTime;

import com.example.tsumugu.entity.Photo;

import lombok.Getter;


// Postやユーザー情報は含めず写真自体の情報だけを返す
@Getter
public class PhotoResponse {
	
	private final Long id;
	private final Long postId;
	private final String imagePath;
	private final String comment;
	private final LocalDateTime createdAt;
	
	public PhotoResponse(Photo photo) {
		this.id = photo.getId();
		this.postId = photo.getPost().getId();
		this.imagePath = photo.getImagePath();
		this.comment = photo.getComment();
		this.createdAt = photo.getCreatedAt();
	}
}
