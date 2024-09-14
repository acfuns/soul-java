package com.github.acfuns.soul.controller;

import com.github.acfuns.soul.domain.postAggregate.LikeRepository;
import com.github.acfuns.soul.domain.postAggregate.Post;
import com.github.acfuns.soul.domain.postAggregate.PostRepository;
import com.github.acfuns.soul.domain.service.LikeService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final LikeRepository likeRepository;
    private final LikeService likeService;

    @Autowired
    public PostController(PostRepository postRepository, LikeRepository likeRepository, LikeService likeService) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.likeService = likeService;
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
                    likeService.getLikeCount(post.getId().toString())
            );
            postVDs.add(postVD);
        }
        return postVDs;
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<String> deletePost(@AuthenticationPrincipal Jwt jwt, @RequestBody PostIDDto postIdDto) {
        var userId = UUID.fromString(jwt.getClaimAsString("sub"));
        var postId = postIdDto.postId;

        var deleteCode = postRepository.deletePostByIdAndUserId(userId, UUID.fromString(postId));

        if (deleteCode > 0) {
            return ResponseEntity.ok("Deleted post successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Deleted post failed");
        }
    }

    @PostMapping("/comment")
    public Post comment(@AuthenticationPrincipal Jwt jwt, @RequestBody CommentDto commentDto) {
        var userId = UUID.fromString(jwt.getClaimAsString("sub"));
        var parentPost = postRepository.findById(UUID.fromString(commentDto.postID)).orElseThrow();
        var comment = new Post();
        comment.setContent(commentDto.content);
        comment.setUserId(userId);
        comment.setParentPost(parentPost);
        return postRepository.save(comment);
    }

    @Transactional
    @PostMapping("/like")
    public ResponseEntity<String> likePost(@AuthenticationPrincipal Jwt jwt, @RequestBody PostIDDto postIdDto) {
        var userId = jwt.getClaimAsString("sub");
        var postId = postIdDto.postId;
        likeService.likePost(postId, userId);
        return ResponseEntity.ok("Post liked successfully");
    }

    @Transactional
    @PostMapping("/unlike")
    public ResponseEntity<String> unLikePost(@AuthenticationPrincipal Jwt jwt, @RequestBody PostIDDto postIdDto) {
        var userId = jwt.getClaimAsString("sub");
        var postId = postIdDto.postId;
        likeService.unlikePost(postId, userId);
        return ResponseEntity.ok("Post unliked successfully");
    }

    public record PostIDDto(String postId) {}

    public record PostDto(String content) {}

    public record PostVD(
            UUID postId,
            String content,
            UUID userId,
            Post parentPost,
            long comment_at,
            long like_at
    ) {}

    public record CommentDto(String postID, String content) {}
}
