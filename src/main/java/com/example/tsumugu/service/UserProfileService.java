package com.example.tsumugu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tsumugu.controller.UserProfileResponse;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.exception.ResourceNotFoundException;
import com.example.tsumugu.repository.PostRepository;
import com.example.tsumugu.repository.UserRepository;

/**
 * ユーザーのプロフィールと、そのユーザーの投稿一覧を扱うサービス。
 */
@Service
public class UserProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostAccessService postAccessService;
    private final FollowService followService;
    private final CurrentUserService currentUserService;

    public UserProfileService(UserRepository userRepository, PostRepository postRepository,
            PostAccessService postAccessService, FollowService followService,
            CurrentUserService currentUserService) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.postAccessService = postAccessService;
        this.followService = followService;
        this.currentUserService = currentUserService;
    }

    // プロフィールを取得する
    public UserProfileResponse getProfile(Long userId) {
        User target = findUser(userId);
        User me = currentUserService.getCurrentUser();

        boolean isMe = target.getId().equals(me.getId());
        FollowStatus followStatus = isMe ? FollowStatus.NONE : followService.getStatus(userId);
        boolean canViewPosts = postAccessService.canViewAccount(target, me);

        return new UserProfileResponse(target, isMe, followStatus, canViewPosts);
    }

    // そのユーザーの投稿のうち、見られるものを新しい順に20件取得する
    public List<Post> getPosts(Long userId, Long beforeId) {
        User target = findUser(userId);
        User me = currentUserService.getCurrentUser();

        // 本人は、非公開の投稿も含めて全部見られる
        if (target.getId().equals(me.getId())) {
            return beforeId == null
                    ? postRepository.findTop20ByUserOrderByIdDesc(target)
                    : postRepository.findTop20ByUserAndIdLessThanOrderByIdDesc(target, beforeId);
        }

        // 鍵アカウントで、フォロワーでない人には何も返さない
        if (!postAccessService.canViewAccount(target, me)) {
            return List.of();
        }

        // それ以外の人には、公開投稿だけを返す
        return beforeId == null
                ? postRepository.findTop20ByUserAndIsPublicTrueOrderByIdDesc(target)
                : postRepository.findTop20ByUserAndIsPublicTrueAndIdLessThanOrderByIdDesc(target, beforeId);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("ユーザーが見つかりません"));
    }
}
