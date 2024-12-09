package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import org.mapstruct.Mapper;

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
                .title(post.getTitle())
                .content(post.getContent())
                .viewCounts(post.getViewCounts())
                .build();
    }
}
