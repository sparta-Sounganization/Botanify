package com.sounganization.botanify.domain.community.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.community.dto.req.PostReqDto;
import com.sounganization.botanify.domain.community.dto.req.PostUpdateReqDto;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.dto.res.PostWithCommentResDto;
import com.sounganization.botanify.domain.community.service.PostService;
import com.sounganization.botanify.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class PostControllerTest {

    // Controller 테스트를 위한 MockMvc 주입
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private PostService postService;

    // dto 직렬화 도구
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String serialize(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    private Long userId;
    private PostReqDto reqDto;
    private CommonResDto createdResDto;
    private CommonResDto updatedResDto;
    private Page<PostListResDto> resDtos;
    private PostWithCommentResDto resDto;

    @BeforeEach
    void setUp() {
        userId = 1L;
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        reqDto = new PostReqDto("test title", "test content");
        createdResDto = new CommonResDto(HttpStatus.CREATED,"test message");
        updatedResDto = new CommonResDto(HttpStatus.OK,"test message");
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId,
                "test user",
                "test@email",
                "test pw",
                "test city",
                "test town",
                UserRole.USER);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        resDtos = new PageImpl<>(List.of(),PageRequest.of(1,10),0);
        resDto = mock(PostWithCommentResDto.class);
    }

    @Test
    void createPost_Success() throws Exception {
        // given
        given(postService.createPost(any(PostReqDto.class),eq(userId))).willReturn(createdResDto);

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(reqDto)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("test message"))
                .andDo(print());
    }

    @Test
    void readPosts_Success() throws Exception {
        // given
        given(postService.readPosts(1,10)).willReturn(resDtos);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.page.size").value(10))
                .andDo(print());
    }

    @Test
    void readPost_Success() throws Exception {
        // given
        Long postId = 1L;
        given(postService.readPost(postId, null)).willReturn(resDto);

        // when
        ResultActions result = mockMvc.perform(get("/api/v1/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andExpect(content().json(serialize(resDto)))
                .andDo(print());
    }

    @Test
    void updatePost_Success() throws Exception {
        // given
        Long postId = 1L;
        given(postService.updatePost(eq(postId), any(PostUpdateReqDto.class), eq(userId))).willReturn(updatedResDto);

        // when
        ResultActions result = mockMvc.perform(put("/api/v1/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(reqDto)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("test message"))
                .andDo(print());
    }

    @Test
    void deletePost_Success() throws Exception {
        // given
        Long postId = 1L;
        doNothing().when(postService).deletePost(postId, userId);

        // when
        ResultActions result = mockMvc.perform(delete("/api/v1/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());
        verify(postService).deletePost(postId, userId);
    }
}