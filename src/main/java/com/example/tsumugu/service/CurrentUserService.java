package com.example.tsumugu.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.UserRepository;
import com.example.tsumugu.security.CustomUserDetails;

/**
 * ログイン中のユーザーを取得するサービス。
 */
@Service
public class CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getCurrentUser() {
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        // セッションに入っているUserはログイン時点の古い情報なので、DBから取り直す
        return userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
    }
}