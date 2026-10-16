package com.example.tsumugu.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.tsumugu.entity.Post;
import com.example.tsumugu.entity.User;
import com.example.tsumugu.exception.BadRequestException;
import com.example.tsumugu.repository.PostRepository;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CurrentUserService currentUserService;

    public PostService(PostRepository postRepository, CurrentUserService currentUserService) {
        this.postRepository = postRepository;
        this.currentUserService = currentUserService;
    }
    
    public Post createOrUpdateTodayPost(String diaryText, Integer mood, Double sleepHours, Boolean isPublic) {
        User currentUser = currentUserService.getCurrentUser();
        LocalDate today = LocalDate.now();

        Post post = postRepository.findByUserAndDate(currentUser, today)
                .orElse(new Post());
        boolean isNew = post.getId() == null;

        post.setUser(currentUser);
        post.setDate(today);
        post.setDiaryText(diaryText);
        post.setMood(mood);
        post.setSleepHours(sleepHours);

        if (isPublic != null) {
            // 明示的に指定されたらそれに従う
            post.setPublic(isPublic);
        } else if (isNew) {
            // 指定がない新規投稿は、直前の投稿の設定を引き継ぐ(初回は非公開)
            boolean inherited = postRepository
                    .findFirstByUserAndDateBeforeOrderByDateDesc(currentUser, today)
                    .map(Post::isPublic)
                    .orElse(false);
            post.setPublic(inherited);
        }

        LocalDateTime now = LocalDateTime.now();
        if (isNew) {
            post.setCreatedAt(now);
        }
        post.setUpdatedAt(now);

        return postRepository.save(post);
    }
    
    public Optional<Post> getTodayPost() {
        User currentUser = currentUserService.getCurrentUser();
        LocalDate today = LocalDate.now();
        return postRepository.findByUserAndDate(currentUser, today);
    }
    
    // 投稿の公開設定を変更する
    public Post updateVisibility(Post post, boolean isPublic) {
        post.setPublic(isPublic);
        post.setUpdatedAt(LocalDateTime.now());
        return postRepository.save(post);
    }
    
    // 公開タイムラインを取得する(beforeIdがあれば、そのIDより古い投稿の続き)
    public List<Post> getTimeline(Long beforeId) {
        if (beforeId == null) {
            return postRepository.findTop20ByIsPublicTrueAndUserIsPrivateFalseOrderByIdDesc();
        }
        return postRepository.findTop20ByIsPublicTrueAndUserIsPrivateFalseAndIdLessThanOrderByIdDesc(beforeId);
    }
    
 // カレンダー用:指定した月の、自分の投稿を取得する(省略したら今月)
    public List<Post> getCalendarPosts(Integer year, Integer month) {
        YearMonth target;

        if (year == null && month == null) {
            target = YearMonth.now();
        } else if (year == null || month == null) {
            throw new BadRequestException("yearとmonthは両方指定してください");
        } else if (month < 1 || month > 12) {
            throw new BadRequestException("monthは1〜12で指定してください");
        } else {
            target = YearMonth.of(year, month);
        }

        User me = currentUserService.getCurrentUser();
        return postRepository.findByUserAndDateBetweenOrderByDateAsc(
                me, target.atDay(1), target.atEndOfMonth());
    }
}