package com.example.tsumugu.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 承認されたらこの行を消して、Followに追加する。
@Entity
@Table(name = "follow_requests", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"follower_id", "followee_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FollowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 申請する側のユーザー
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    // 申請される側のユーザー
    @ManyToOne
    @JoinColumn(name = "followee_id", nullable = false)
    private User followee;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}