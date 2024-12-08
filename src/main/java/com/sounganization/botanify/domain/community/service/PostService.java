package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.res.PageDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    // 게시글 작성
    @Transactional
    public PostResDto createPost(PostReqDto postReqDto, Long userId) {

        //dto -> entity
        Post post = postMapper.postReqDtoToPost(postReqDto, userId);

        // DB 저장
        Post savedPost = postRepository.save(post);

        //entity -> dto
        return postMapper.postToPostResDto(savedPost, HttpStatus.CREATED.value(),"게시글이 등록되었습니다");
    }

    // 게시글 조회 - 다건 조회
    public PageDto<PostListResDto> getPosts(int page, int size) {
        //pageable
        Pageable pageable = PageRequest.of(page, size, Sort.by("updatedAt").descending());

        Page<Post> posts = postRepository.findAll(pageable);

        // Page<Post> -> Page<PostListResDto>
        Page<PostListResDto> postDtos = posts.map(post -> postMapper.postToPostListResDto(post));

        // PageDto
        return new PageDto<>(postDtos);
    }

}
