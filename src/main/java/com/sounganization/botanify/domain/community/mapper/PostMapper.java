package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.entity.Comment;
import com.sounganization.botanify.domain.community.entity.Post;
import org.mapstruct.Mapper;

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

    default PostResDto entityToResDto(Post post, int statusCode, String message) {
        return PostResDto.builder()
                .status(statusCode)
                .message(message)
                .postId(post.getId())
                .build();
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
