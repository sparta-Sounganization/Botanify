package com.sounganization.botanify.domain.community.dto.res;

import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor

public class PostWithCommentResDto {
    private final String title;
    private final String content;
    private final Integer viewCounts;

    private final List<CommentResDto> comments;

    public PostWithCommentResDto(Post post, List<Comment> comments) {
        this.title = post.getTitle();
        this.content = post.getContent();
        this.viewCounts = post.getViewCounts();

        this.comments = buildCommentTree(comments);
    }

    // 댓글 트리 구조로 변환하는 메서드
    private List<CommentResDto> buildCommentTree(List<Comment> comments) {
        Map<Long, CommentResDto> commentMap = new HashMap<>();
        List<CommentResDto> rootComments = new ArrayList<>();

        // 댓글을 맵에 저장
        for (Comment comment : comments) {
            CommentResDto commentDto = new CommentResDto(comment);
            commentMap.put(comment.getId(), commentDto);
        }

        // 트리 구조로 부모-자식 관계 설정
        for (Comment comment : comments) {
            if (comment.getParentComment() != null) {
                // 자식 댓글은 부모 댓글의 childComments에 추가
                CommentResDto parentDto = commentMap.get(comment.getParentComment().getId());
                CommentResDto childDto = commentMap.get(comment.getId());
                parentDto.addChildComment(childDto);
            } else {
                // 최상위 댓글은 rootComments에 추가
                rootComments.add(commentMap.get(comment.getId()));
            }
        }

        return rootComments;
    }
}
