package com.example.tsumugu.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Comment;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.CommentRepository;
import com.example.tsumugu.security.CustomUserDetails;

@Service
public class CommentService {
	
	private final CommentRepository commentRepository;
	
	public CommentService(CommentRepository commentRepository) {
		this.commentRepository = commentRepository;
	}
	
	// ログイン中のユーザーを取得する
	private User getCurrentUser() {
		CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
		return userDetails.getUser();
	}
	
	// コメントを投稿する
	public Comment addComment(Post post, String commentText) {
		User currentUser = getCurrentUser();
		
		Comment comment = new Comment();
		comment.setPost(post);
		comment.setUser(currentUser);
		comment.setCommentText(commentText);
		comment.setCreatedAt(LocalDateTime.now());
		comment.setUpdatedAt(LocalDateTime.now());
		
		return commentRepository.save(comment);
	}
	
	// 投稿に紐づくコメント一覧を取得する
	public List<Comment> getComments(Post post) {
		return commentRepository.findByPostOrderByCreatedAtAsc(post);
	}
	
	// コメントを削除する(投稿者本人のみ削除可能)
	public void deleteComment(Long commentId) {
		User currentUser = getCurrentUser();
		
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("コメントが見つかりません"));
		
		if (!comment.getUser().getId().equals(currentUser.getId())) {
			throw new IllegalStateException("自分のコメントのみ削除できます");
		}
		
		commentRepository.delete(comment);
	}
}
