package com.example.tsumugu.service;

import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.FollowRepository;

/**
 * 投稿を閲覧できるかどうかを判定するサービス。
 * いいね・コメント・写真など、投稿に紐づく機能はすべてここで判定する。
 */
@Service
public class PostAccessService {

    private final FollowRepository followRepository;

    public PostAccessService(FollowRepository followRepository) {
        this.followRepository = followRepository;
    }

    // その投稿を見られるかどうか
    public boolean canView(Post post, User viewer) {
        User owner = post.getUser();

        // 本人は自分の投稿を常に見られる
        if (owner.getId().equals(viewer.getId())) {
            return true;
        }

        // 非公開投稿は本人以外には見えない
        if (!post.isPublic()) {
            return false;
        }

        // 公開投稿は、そのアカウントの投稿を見られる立場なら見られる
        return canViewAccount(owner, viewer);
    }

    // そのユーザーの公開投稿を見られる立場かどうか
    public boolean canViewAccount(User owner, User viewer) {
        // 本人
        if (owner.getId().equals(viewer.getId())) {
            return true;
        }

        // 公開アカウントなら誰でも
        if (!owner.isPrivate()) {
            return true;
        }

        // 鍵アカウントならフォロワーのみ
        return followRepository.existsByFollowerAndFollowee(viewer, owner);
    }
}
    