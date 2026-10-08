package com.example.tsumugu.service;

import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;

/**
 * 投稿を閲覧できるかどうかを判定するサービス。
 * いいね・コメント・写真など、投稿に紐づく機能はすべてここで判定する。
 */
@Service
public class PostAccessService {

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

        // 公開アカウントの公開投稿は誰でも見られる
        if (!owner.isPrivate()) {
            return true;
        }

        // 鍵アカウントの公開投稿はフォロワーのみ
        // TODO: フォロー機能の実装時に、ここへフォロワーかどうかの判定を追加する
        return false;
    }
}