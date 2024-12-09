package com.sounganization.botanify.domain.community.dto.res;

import com.sounganization.botanify.domain.community.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentTempDto {
    private Long commentId;
    private Long userId;
    private String username;
    private String content;
    private List<CommentTempDto> childComments;

    public CommentTempDto(Comment comment) {
        this.commentId = comment.getId();
        this.userId = comment.getUserId();
        this.content = comment.getContent();
        this.username = getUsernameByUserId(comment.getUserId());

        this.childComments = new ArrayList<>();}

    public void addChildComment(CommentTempDto childComment) {
        this.childComments.add(childComment);
    }


    //userId로 username 가져오는 메서드
    private String getUsernameByUserId(Long userId) {
        // UserRepository에서 userId로 username을 조회
        // 지금은 임시 userId로
        if (userId == 1L) {
            return "유저1";
        } else if (userId == 2L) {
            return "유저2";
        }
        return "알수없는사용자";
    }
}
