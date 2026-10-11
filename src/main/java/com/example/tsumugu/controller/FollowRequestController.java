package com.example.tsumugu.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.service.FollowService;

@RestController
@RequestMapping("/api/follow-requests")
public class FollowRequestController {

    private final FollowService followService;

    public FollowRequestController(FollowService followService) {
        this.followService = followService;
    }

    // 自分宛ての申請一覧
    @GetMapping
    public ResponseEntity<List<FollowRequestResponse>> getReceivedRequests() {
        List<FollowRequestResponse> responses = followService.getReceivedRequests()
                .stream()
                .map(FollowRequestResponse::new)
                .toList();
        return ResponseEntity.ok(responses);
    }

    // 申請を承認する
    @PostMapping("/{requestId}/approve")
    public ResponseEntity<Void> approve(@PathVariable Long requestId) {
        followService.approve(requestId);
        return ResponseEntity.noContent().build();
    }

    // 申請を却下する
    @DeleteMapping("/{requestId}")
    public ResponseEntity<Void> reject(@PathVariable Long requestId) {
        followService.reject(requestId);
        return ResponseEntity.noContent().build();
    }
}