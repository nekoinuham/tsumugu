package com.example.tsumugu.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;

public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findByUserAndDate(User user, LocalDate date);

    // 指定日より前で、いちばん新しい投稿を取得する(公開設定の引き継ぎ用)
    Optional<Post> findFirstByUserAndDateBeforeOrderByDateDesc(User user, LocalDate date);
    
    // 公開タイムライン:公開アカウントの公開投稿を、新しい順に20件
    List<Post> findTop20ByIsPublicTrueAndUserIsPrivateFalseOrderByIdDesc();
    
    // 公開タイムラインの続き:指定したIDより古いものを、新しい順に20件
    List<Post> findTop20ByIsPublicTrueAndUserIsPrivateFalseAndIdLessThanOrderByIdDesc(Long beforeId);
}