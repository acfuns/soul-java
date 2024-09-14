package com.github.acfuns.soul.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class LikeService {
    private static final String LIKE_KEY = "post_likes:";

    private final RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public LikeService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 点赞帖子
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    public void likePost(String postId, String userId) {
        String key = LIKE_KEY + postId;

        if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(key, userId))) {
            redisTemplate.opsForSet().add(key, userId);
            // 更新点赞数
            redisTemplate.opsForValue().increment("post_like_count" + postId);
        }
    }

    /**
     * 取消点赞
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    public void unlikePost(String postId, String userId) {
        String key = LIKE_KEY + postId;

        if (Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId))) {
            redisTemplate.opsForSet().remove(key, userId);
            // 更新点赞数
            redisTemplate.opsForValue().decrement("post_like_count" + postId);
        }
    }

    public long getLikeCount(String postId) {
        String key = "post_like_count" + postId;
        var postLikeCount = redisTemplate.opsForValue().get(key);
        if (postLikeCount == null) {
            return 0;
        }

        return Long.parseLong(postLikeCount.toString());
    }
}
