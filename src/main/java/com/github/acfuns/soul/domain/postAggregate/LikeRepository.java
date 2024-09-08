package com.github.acfuns.soul.domain.postAggregate;

import org.apache.el.stream.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<Like, UUID> {
    Like findByUserIdAndPostId(UUID userId,UUID postId);
    Void deleteByUserIdAndPostId(UUID userId,UUID postId);
}