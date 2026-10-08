package com.example.tsumugu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Comment;
import com.example.tsumugu.entity.Post;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
	// 指定した投稿に紐づくコメントを一覧取得する
	List<Comment> findByPostOrderByCreatedAtAsc(Post post);
}