package com.example.tsumugu.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tsumugu.entity.Follow;
import com.example.tsumugu.entity.FollowRequest;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.FollowRepository;
import com.example.tsumugu.repository.FollowRequestRepository;
import com.example.tsumugu.repository.UserRepository;

/**
 * フォロー・フォロー申請・解除を扱うサービス。
 */
@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final FollowRequestRepository followRequestRepository;
    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    public FollowService(FollowRepository followRepository,
            FollowRequestRepository followRequestRepository,
            UserRepository userRepository,
            CurrentUserService currentUserService) {
        this.followRepository = followRepository;
        this.followRequestRepository = followRequestRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
    }

    // フォローする(相手が公開アカウントなら即フォロー、鍵アカウントなら申請になる)
    @Transactional
    public FollowStatus follow(Long targetUserId) {
        User me = currentUserService.getCurrentUser();
        User target = findUser(targetUserId);

        if (me.getId().equals(target.getId())) {
            throw new IllegalArgumentException("自分自身はフォローできません");
        }

        // すでにフォロー中・申請中なら、何も追加せずに今の状態を返す
        if (followRepository.existsByFollowerAndFollowee(me, target)) {
            return FollowStatus.FOLLOWING;
        }
        if (followRequestRepository.existsByFollowerAndFollowee(me, target)) {
            return FollowStatus.REQUESTED;
        }

        LocalDateTime now = LocalDateTime.now();

        if (target.isPrivate()) {
            FollowRequest request = new FollowRequest();
            request.setFollower(me);
            request.setFollowee(target);
            request.setCreatedAt(now);
            followRequestRepository.save(request);
            return FollowStatus.REQUESTED;
        }

        Follow follow = new Follow();
        follow.setFollower(me);
        follow.setFollowee(target);
        follow.setCreatedAt(now);
        followRepository.save(follow);
        return FollowStatus.FOLLOWING;
    }

    // フォローを解除する
    @Transactional
    public void unfollow(Long targetUserId) {
        User me = currentUserService.getCurrentUser();
        User target = findUser(targetUserId);

        followRepository.findByFollowerAndFollowee(me, target)
                .ifPresent(followRepository::delete);
    }

    // 自分が出した申請を取り消す
    @Transactional
    public void cancelRequest(Long targetUserId) {
        User me = currentUserService.getCurrentUser();
        User target = findUser(targetUserId);

        followRequestRepository.findByFollowerAndFollowee(me, target)
                .ifPresent(followRequestRepository::delete);
    }

    // 相手との今の関係を調べる(プロフィール画面のボタンの出し分け用)
    public FollowStatus getStatus(Long targetUserId) {
        User me = currentUserService.getCurrentUser();
        User target = findUser(targetUserId);

        if (followRepository.existsByFollowerAndFollowee(me, target)) {
            return FollowStatus.FOLLOWING;
        }
        if (followRequestRepository.existsByFollowerAndFollowee(me, target)) {
            return FollowStatus.REQUESTED;
        }
        return FollowStatus.NONE;
    }
}