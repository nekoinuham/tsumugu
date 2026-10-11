package com.example.tsumugu.controller;

import java.time.LocalDateTime;

import com.example.tsumugu.entity.FollowRequest;

import lombok.Getter;

@Getter
public class FollowRequestResponse {

	private final Long id;
	private final Long followerId;
	private final String followerDisplayName;
	private final LocalDateTime createdAt;
	
	public FollowRequestResponse(FollowRequest request) {
		this.id = request.getId();
		this.followerId = request.getFollower().getId();
		this.followerDisplayName = request.getFollower().getDisplayName();
		this.createdAt = request.getCreatedAt();
	}
}
