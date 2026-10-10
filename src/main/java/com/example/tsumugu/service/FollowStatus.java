package com.example.tsumugu.service;

/**
 * 自分から見た、相手とのフォロー関係。
 */
public enum FollowStatus {
    NONE,        // フォローしていない
    REQUESTED,   // 申請中(相手が鍵アカウントで、承認待ち)
    FOLLOWING    // フォロー中
}