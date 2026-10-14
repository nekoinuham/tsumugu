package com.example.tsumugu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.tsumugu.exception.BadRequestException;
import com.example.tsumugu.service.CurrentUserService;
import com.example.tsumugu.service.UserService;

@RestController
@RequestMapping("/api/users/me")
public class ProfileController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public ProfileController(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    // 鍵アカウントのON/OFFを切り替える
    @PatchMapping("/privacy")
    public ResponseEntity<Boolean> updatePrivacy(@RequestBody PrivacyRequest request) {
        if (request.getIsPrivate() == null) {
            throw new BadRequestException("isPrivateを指定してください");
        }

        boolean result = userService.updatePrivacy(request.getIsPrivate());
        return ResponseEntity.ok(result);
    }
    
    // ログイン中の自分の情報を取得する
    @GetMapping
    public ResponseEntity<MeResponse> getMe() {
    	return ResponseEntity.ok(new MeResponse(currentUserService.getCurrentUser()));
    }
}