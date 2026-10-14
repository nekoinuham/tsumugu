package com.example.tsumugu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.service.PostResponseService;
import com.example.tsumugu.service.PostService;

@RestController
@RequestMapping("/api/timeline")
public class TimelineController {

	private final PostService postService;
	private final PostResponseService postResponseService;
	
	public TimelineController(PostService postService, PostResponseService postResponseService) {
		this.postService = postService;
		this.postResponseService = postResponseService;
	}
	
	// 公開タイムラインを取得する
	// beforeIdを付けると、そのIDより古い投稿を返す(もっと見る)
	@GetMapping
	public ResponseEntity<List<PostResponse>> getTimeline(
			@RequestParam(required = false) Long beforeId) {
		
		List<Post> posts = postService.getTimeline(beforeId);
		return ResponseEntity.ok(postResponseService.toResponses(posts));
	}
}
