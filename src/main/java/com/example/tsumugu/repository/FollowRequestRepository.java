package com.example.tsumugu.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.tsumugu.entity.FollowRequest;
import com.example.tsumugu.entity.User;

public interface FollowRequestRepository extends JpaRepository<FollowRequest, Long> {

    // この2人の間に、申請中のものがあるかを調べる(取り消し・承認・却下で使う)
    Optional<FollowRequest> findByFollowerAndFollowee(User follower, User followee);

    boolean existsByFollowerAndFollowee(User follower, User followee);

    // 自分宛ての申請一覧(古い順)
    List<FollowRequest> findByFolloweeOrderByCreatedAtAsc(User followee);
}