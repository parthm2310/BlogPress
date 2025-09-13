package com.blogspot.engagement.service;

import com.blogspot.engagement.dto.CommentDtos.CommentResponse;
import com.blogspot.engagement.dto.CommentDtos.CreateCommentRequest;
import com.blogspot.engagement.dto.CommentDtos.UpdateCommentRequest;
import com.blogspot.engagement.model.CommentEntity;
import com.blogspot.engagement.model.LikeEntity;
import com.blogspot.engagement.model.ViewEntity;
import com.blogspot.engagement.repository.CommentRepository;
import com.blogspot.engagement.repository.LikeRepository;
import com.blogspot.engagement.repository.ViewRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EngagementServiceImpl implements EngagementService {

    private final LikeRepository likeRepository;
    private final ViewRepository viewRepository;
    private final CommentRepository commentRepository;

    // Likes
    @Override
    @Transactional
    public boolean likeBlog(Long blogId, String username) {
        if (likeRepository.existsByBlogIdAndUsername(blogId, username)) {
            return false; // already liked
        }
        LikeEntity like = LikeEntity.builder()
            .blogId(blogId)
            .username(username)
            .build();
        likeRepository.save(like);
        return true;
    }

    @Override
    @Transactional
    public boolean unlikeBlog(Long blogId, String username) {
        Optional<LikeEntity> existing = likeRepository.findByBlogIdAndUsername(blogId, username);
        if (existing.isEmpty()) {
            return false;
        }
        likeRepository.delete(existing.get());
        return true;
    }

    @Override
    @Transactional
    public boolean toggleLike(Long blogId, String username) {
        if (likeRepository.existsByBlogIdAndUsername(blogId, username)) {
            // User has already liked, so unlike
            Optional<LikeEntity> existing = likeRepository.findByBlogIdAndUsername(blogId, username);
            if (existing.isPresent()) {
                likeRepository.delete(existing.get());
                return false; // unliked
            }
        } else {
            // User hasn't liked, so like
            LikeEntity like = LikeEntity.builder()
                .blogId(blogId)
                .username(username)
                .build();
            likeRepository.save(like);
            return true; // liked
        }
        return false;
    }

    @Override
    public boolean isLiked(Long blogId, String username) {
        return likeRepository.existsByBlogIdAndUsername(blogId, username);
    }

    @Override
    public long getLikeCount(Long blogId) {
        return likeRepository.countByBlogId(blogId);
    }

    // Views
    @Override
    @Transactional
    public void recordView(Long blogId, String username, String ipAddress) {
        ViewEntity view = ViewEntity.builder()
            .blogId(blogId)
            .username(username)
            .ipAddress(ipAddress)
            .build();
        viewRepository.save(view);
    }

    @Override
    public long getViewCount(Long blogId) {
        return viewRepository.countByBlogId(blogId);
    }

    // Comments
    @Override
    @Transactional
    public CommentResponse addComment(String username, CreateCommentRequest request) {
        CommentEntity parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                .orElseThrow(() -> new EntityNotFoundException("Parent comment not found"));
        }
        CommentEntity entity = CommentEntity.builder()
            .blogId(request.getBlogId())
            .username(username)
            .content(request.getContent())
            .parent(parent)
            .build();
        CommentEntity saved = commentRepository.save(entity);
        return toResponse(saved, true);
    }

    @Override
    @Transactional
    public CommentResponse updateComment(Long commentId, String username, UpdateCommentRequest request) {
        CommentEntity entity = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (!entity.getUsername().equals(username)) {
            throw new SecurityException("You can only update your own comments");
        }
        entity.setContent(request.getContent());
        CommentEntity saved = commentRepository.save(entity);
        return toResponse(saved, true);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, String username) {
        CommentEntity entity = commentRepository.findById(commentId)
            .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        if (!entity.getUsername().equals(username)) {
            throw new SecurityException("You can only delete your own comments");
        }
        commentRepository.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsTree(Long blogId) {
        List<CommentEntity> roots = commentRepository.findByBlogIdAndParentIsNullOrderByCreatedAtAsc(blogId);
        return roots.stream()
            .map(root -> toResponse(root, true))
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getCommentCount(Long blogId) {
        return commentRepository.countByBlogId(blogId);
    }

    private CommentResponse toResponse(CommentEntity entity, boolean includeReplies) {
        CommentResponse response = CommentResponse.builder()
            .id(entity.getId())
            .blogId(entity.getBlogId())
            .username(entity.getUsername())
            .content(entity.getContent())
            .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
        if (includeReplies) {
            List<CommentEntity> children = commentRepository.findByParentIdOrderByCreatedAtAsc(entity.getId());
            List<CommentResponse> childResponses = new ArrayList<>();
            for (CommentEntity child : children) {
                childResponses.add(toResponse(child, true));
            }
            response.setReplies(childResponses);
        }
        return response;
    }
}


