package com.example.tsumugu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.service.FollowService;
import com.example.tsumugu.service.FollowStatus;

@RestController
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    // フォローする(相手が鍵アカウントなら申請になる)
    @PostMapping("/{userId}/follow")
    public ResponseEntity<FollowStatus> follow(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.follow(userId));
    }

    // フォローを解除する
    @DeleteMapping("/{userId}/follow")
    public ResponseEntity<Void> unfollow(@PathVariable Long userId) {
        followService.unfollow(userId);
        return ResponseEntity.noContent().build();
    }

    // 自分が出した申請を取り消す
    @DeleteMapping("/{userId}/follow-request")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long userId) {
        followService.cancelRequest(userId);
        return ResponseEntity.noContent().build();
    }

    // 相手との今の関係を取得する
    @GetMapping("/{userId}/follow-status")
    public ResponseEntity<FollowStatus> getStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(followService.getStatus(userId));
    }
}