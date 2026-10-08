package com.example.tsumugu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.service.UserService;

@RestController
@RequestMapping("/api/users/me")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    // 鍵アカウントのON/OFFを切り替える
    @PatchMapping("/privacy")
    public ResponseEntity<Boolean> updatePrivacy(@RequestBody PrivacyRequest request) {
        if (request.getIsPrivate() == null) {
            return ResponseEntity.badRequest().build();
        }

        boolean result = userService.updatePrivacy(request.getIsPrivate());
        return ResponseEntity.ok(result);
    }
}