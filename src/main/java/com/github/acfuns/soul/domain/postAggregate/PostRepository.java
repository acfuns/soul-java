package com.github.acfuns.soul.domain.postAggregate;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findPostsByUserId(UUID userId);

    Integer deletePostByIdAndUserId(UUID id, UUID userId);
}