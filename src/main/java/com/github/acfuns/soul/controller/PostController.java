package com.github.acfuns.soul.controller;

import com.github.acfuns.soul.domain.postAggregate.LikeRepository;
import com.github.acfuns.soul.domain.postAggregate.Post;
import com.github.acfuns.soul.domain.postAggregate.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostRepository postRepository;
    // private final LikeRepository likeRepository;

    public PostController(@Autowired PostRepository postRepository, @Autowired LikeRepository likeRepository) {
        this.postRepository = postRepository;
        // this.likeRepository = likeRepository;
    }

    /**
     * 创建帖子
     */
    @PostMapping
    public Post createPost(@AuthenticationPrincipal Jwt jwt, @RequestBody PostDto postDto) {
        var userId = UUID.fromString(jwt.getClaimAsString("sub"));
        log.info("post userId: {}, post content: {}", userId, postDto.content);
        var post = new Post();
        post.setContent(postDto.content);
        post.setUserId(userId);
        return postRepository.save(post);
    }

    /**
     * 查找用户相关帖子
     * @param jwt 用户jwt token claims信息
     * @return 相关帖子
     */
    @GetMapping
    public List<PostVD> getPostsByUserID(@AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getClaimAsString("sub"));
        List<Post> posts = postRepository.findPostsByUserId(userId);
        List<PostVD> postVDs = new ArrayList<>();

        for (Post post : posts) {
            var postVD = new PostVD(
                    post.getId(),
                    post.getContent(),
                    post.getUserId(),
                    post.getParentPost(),
                    post.getPosts().size(),
                    post.getLikes().size()
            );
            postVDs.add(postVD);
        }
        return postVDs;
    }


    public record PostDto(String content) {
    }

    public record PostVD(
            UUID postId,
            String content,
            UUID userId,
            Post parentPost,
            int comment_at,
            int like_at
    ) {
    }
}
