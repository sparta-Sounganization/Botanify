package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import org.mapstruct.Mapper;

@Mapper(componentModel = "Spring")
public interface PostMapper {

    default Post postReqDtoToPost(PostReqDto postReqDto, Long userId) {
        return new Post (
        postReqDto.getTitle(),
        postReqDto.getContent(),
        userId);
    }

    default PostResDto postToPostResDto(Post post, int statusCode, String message) {
        return new PostResDto(
                statusCode,
                message,
                post.getId()
        );
    }

    default PostListResDto postToPostListResDto(Post post) {
        return new PostListResDto(
                post.getTitle(),
                post.getContent(),
                post.getViewCounts()
        );
    }
}
