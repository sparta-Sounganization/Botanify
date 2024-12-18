package com.sounganization.botanify.domain.community.mapper;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.CommentTempDto;
import com.sounganization.botanify.domain.community.dto.res.PopularPostResDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import org.mapstruct.Mapper;
import org.springframework.http.HttpStatus;

import java.util.List;

@Mapper(componentModel = "Spring")
public interface PostMapper {

    default Post reqDtoToEntity(PostReqDto postReqDto, Long userId) {
        return Post.builder()
                .title(postReqDto.title())
                .content(postReqDto.content())
                .viewCounts(0)
                .userId(userId)
                .imageUrl(postReqDto.imageUrl())
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
                .imageUrl(post.getImageUrl())
                .build();
    }

    default PostWithCommentResDto entityToResDto(Post post, List<CommentTempDto> comments) {
        return PostWithCommentResDto.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .viewCounts(post.getViewCounts())
                .imageUrl(post.getImageUrl())
                .comments(comments)
                .build();
    }

    default PopularPostResDto entityToPopularDto(Post post, Integer commentCount, Double score) {
        return new PopularPostResDto(
                post.getId(),
                post.getTitle(),
                post.getViewCounts(),
                post.getImageUrl(),
                commentCount,
                score
        );
    }
}
