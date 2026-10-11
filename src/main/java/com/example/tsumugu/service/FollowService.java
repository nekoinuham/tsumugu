package com.example.tsumugu.service;

import java.time.LocalDateTime;
import java.util.List;

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
    
 // 自分宛ての申請一覧を取得する
    public List<FollowRequest> getReceivedRequests() {
        User me = currentUserService.getCurrentUser();
        return followRequestRepository.findByFolloweeOrderByCreatedAtAsc(me);
    }

    // 申請を承認する(申請された本人のみ)
    @Transactional
    public void approve(Long requestId) {
        FollowRequest request = findOwnRequest(requestId);
        convertToFollow(request);
    }

    // 申請を却下する(申請された本人のみ)
    @Transactional
    public void reject(Long requestId) {
        FollowRequest request = findOwnRequest(requestId);
        followRequestRepository.delete(request);
    }

    // フォロワーを外す(自分をフォローしている人を解除する)
    @Transactional
    public void removeFollower(Long followerUserId) {
        User me = currentUserService.getCurrentUser();
        User follower = findUser(followerUserId);

        followRepository.findByFollowerAndFollowee(follower, me)
                .ifPresent(followRepository::delete);
    }

    // 鍵アカウントを公開に切り替えたとき、申請中をすべて承認する
    @Transactional
    public void approveAllRequests(User followee) {
        followRequestRepository.findByFolloweeOrderByCreatedAtAsc(followee)
                .forEach(this::convertToFollow);
    }

    // 自分宛ての申請を取得する(他人宛ての申請は操作できない)
    private FollowRequest findOwnRequest(Long requestId) {
        User me = currentUserService.getCurrentUser();
        FollowRequest request = followRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("申請が見つかりません"));

        if (!request.getFollowee().getId().equals(me.getId())) {
            throw new IllegalStateException("自分宛ての申請のみ操作できます");
        }
        return request;
    }

    // 申請を承認済みのフォローに変える(承認と、公開への自動承認で共通)
    private void convertToFollow(FollowRequest request) {
        User follower = request.getFollower();
        User followee = request.getFollowee();

        if (!followRepository.existsByFollowerAndFollowee(follower, followee)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowee(followee);
            follow.setCreatedAt(LocalDateTime.now());
            followRepository.save(follow);
        }
        followRequestRepository.delete(request);
    }
}