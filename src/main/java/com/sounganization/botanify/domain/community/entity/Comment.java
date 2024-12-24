package com.sounganization.botanify.domain.community.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Comment> childComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Integer depth;

    public static class CommentBuilder {
        private Integer depth = 0;
    }

    public void update(String content) {
        if(content == null || content.trim().isEmpty()) {
            throw new CustomException(ExceptionStatus.INVALID_COMMENT_CONTENT);
        }
        this.content = content;
    }

    public void softDelete() {
        // 부모 댓글 soft delete
        super.softDelete();

        // 모든 자식 댓글을 재귀적으로 soft delete
        if (!childComments.isEmpty()) {
            childComments.forEach(Comment::softDelete);
        }
    }
}
