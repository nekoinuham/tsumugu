package com.example.tsumugu.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Comment;
import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.repository.CommentRepository;

@Service
public class CommentService {
	
	private final CommentRepository commentRepository;
	private final CurrentUserService currentUserService;
	
	public CommentService(CommentRepository commentRepository, CurrentUserService currentUserService) {
		this.commentRepository = commentRepository;
		this.currentUserService = currentUserService;
	}
	
	// コメントを投稿する
	public Comment addComment(Post post, String commentText) {
		User currentUser = currentUserService.getCurrentUser();
		
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
	
	// コメントを削除する(コメントした本人、または投稿の持ち主)
	public void deleteComment(Long commentId) {
	    User currentUser = currentUserService.getCurrentUser();

	    Comment comment = commentRepository.findById(commentId)
	            .orElseThrow(() -> new IllegalArgumentException("コメントが見つかりません"));

	    boolean isCommentAuthor = comment.getUser().getId().equals(currentUser.getId());
	    boolean isPostOwner = comment.getPost().getUser().getId().equals(currentUser.getId());

	    if (!isCommentAuthor && !isPostOwner) {
	        throw new IllegalStateException("コメントを削除できるのは、コメントした本人か投稿の持ち主のみです");
	    }

	    commentRepository.delete(comment);
	}
}
