package com.example.tsumugu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tsumugu.controller.PostResponse;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.CommentRepository;
import com.example.tsumugu.repository.LikeRepository;

@Service
public class PostResponseService {

    private final PhotoService photoService;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CurrentUserService currentUserService;

    public PostResponseService(PhotoService photoService, LikeRepository likeRepository,
            CommentRepository commentRepository, CurrentUserService currentUserService) {
        this.photoService = photoService;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.currentUserService = currentUserService;
    }

    // 投稿1件をレスポンスにする
    public PostResponse toResponse(Post post) {
        User me = currentUserService.getCurrentUser();
        return toResponse(post, me);
    }

    // 投稿を複数件まとめてレスポンスにする(タイムラインなど)
    public List<PostResponse> toResponses(List<Post> posts) {
        // ログイン中のユーザーの取得はDBを読むので、件数分ではなく1回だけにする
        User me = currentUserService.getCurrentUser();
        return posts.stream()
                .map(post -> toResponse(post, me))
                .toList();
    }

    private PostResponse toResponse(Post post, User me) {
        return new PostResponse(
                post,
                photoService.getPhotos(post),
                likeRepository.countByPost(post),
                likeRepository.existsByPostAndUser(post, me),
                commentRepository.countByPost(post));
    }
}