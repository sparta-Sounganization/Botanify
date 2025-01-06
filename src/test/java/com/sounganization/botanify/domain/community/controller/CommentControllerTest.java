package com.sounganization.botanify.domain.community.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.community.dto.req.CommentReqDto;
import com.sounganization.botanify.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.Collections;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class CommentControllerTest {

    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private WebApplicationContext wac;

    private CommonResDto commonResDto;


    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();

        UserDetailsImpl mockUser = new UserDetailsImpl(1L, "test@email.com","testUser","testPW","서울시","동작구", UserRole.USER,"","");
        GrantedAuthority authority = new SimpleGrantedAuthority("USER");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(mockUser, null, Collections.singleton(authority)));

        // 로그 찍기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            System.out.println("Authenticated User: " + authentication.getName());
            System.out.println("Authorities: " + authentication.getAuthorities());
        } else {
            System.out.println("No authenticated user.");
        }
    }

    @Test
    void createComment() throws Exception {
        CommentReqDto commentReqDto = new CommentReqDto("댓글등록");

        this.mockMvc
                .perform(post("/api/v1/comments/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentReqDto))
                )
                .andExpect(status().isCreated())
                .andDo(print());
    }


}
