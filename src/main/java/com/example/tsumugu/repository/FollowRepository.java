package com.example.tsumugu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.Follow;
import com.example.tsumugu.entity.User;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    // この2人の間に、フォロー関係があるかを調べる(解除のときにも使う)
    Optional<Follow> findByFollowerAndFollowee(User follower, User followee);

    // フォローしているかどうかだけを調べる(閲覧権限の判定用)
    boolean existsByFollowerAndFollowee(User follower, User followee);
}