package com.github.acfuns.soul.application;

import com.github.acfuns.soul.domain.postAggregate.Like;
import com.github.acfuns.soul.domain.postAggregate.LikeRepository;
import com.github.acfuns.soul.domain.postAggregate.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

@Service
public class LikePersistenceService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;


    @Autowired
    public LikePersistenceService(RedisTemplate<String, Object> redisTemplate, PostRepository postRepository, LikeRepository likeRepository) {
        this.redisTemplate = redisTemplate;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
    }

    private static final String LIKE_KEY = "post_likes:";

    @Scheduled(cron = "0 */5 * * * *")
    public void persistLikeData() {
        Set<String> keys = redisTemplate.keys(LIKE_KEY + "*");
        if (keys != null) {
            for (String key : keys) {
                String postId = key.split(":")[1];
                var post = postRepository.findById(UUID.fromString(postId)).orElseThrow();

                Set<Object> likeRecords = redisTemplate.opsForSet().members(key);
                likeRepository.removeAllByPost(post);
                if (likeRecords != null) {
                    for (Object userId : likeRecords) {
                        String likeRecord = (String) userId;
                        Like like = new Like();
                        like.setUserId(UUID.fromString(likeRecord));
                        like.setPost(post);
                        likeRepository.save(like);
                    }
                }
            }
        }
    }
}
