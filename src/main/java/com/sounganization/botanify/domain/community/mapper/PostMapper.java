package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import org.mapstruct.Mapper;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;

@Mapper(componentModel = "Spring")
public interface PostMapper {

    default Post reqDtoToEntity(PostReqDto postReqDto, Long userId) {
        return Post.builder()
                .title(postReqDto.getTitle())
                .content(postReqDto.getContent())
                .viewCounts(0)
                .userId(userId)
                .build();
    }

    default CommonResDto entityToResDto(Post post, HttpStatus status) {
        return new CommonResDto(status, "게시글이 등록되었습니다.", post.getId());
    }

    default PostListResDto entityToResDto(Post post) {
        return PostListResDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCounts(post.getViewCounts())
                .build();
    }

    default PostWithCommentResDto entityToResDto(Post post, List<Comment> comments, Map<Long, String> usernameMap) {
        return PostWithCommentResDto.from(post, comments, usernameMap);
    }
}
