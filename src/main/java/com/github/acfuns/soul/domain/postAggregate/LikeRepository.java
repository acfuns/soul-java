package com.github.acfuns.soul.domain.postAggregate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    Like findByUserIdAndPostId(UUID userId,UUID postId);
    void deleteByUserIdAndPostId(UUID userId, UUID postId);

    void removeAllByPost(Post post);
}