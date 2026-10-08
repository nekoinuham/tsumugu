package com.example.tsumugu.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUserAndDate(User user, LocalDate date);

 // 指定日より前で、いちばん新しい投稿を取得する(公開設定の引き継ぎ用)
    Optional<Post> findFirstByUserAndDateBeforeOrderByDateDesc(User user, LocalDate date);
}