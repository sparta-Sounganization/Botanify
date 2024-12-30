package com.sounganization.botanify.domain.garden.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sounganization.botanify.BotanifyApplication;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
import com.sounganization.botanify.domain.garden.service.PlantService;
import com.sounganization.botanify.domain.user.enums.UserRole;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;


import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
        classes = {BotanifyApplication.class},
        excludeAutoConfiguration = {com.sounganization.botanify.common.config.s3.S3Config.class}
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
class PlantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlantService plantService; // 실제 Bean 주입

    @Autowired
    private WebApplicationContext context;

    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private Long userId;
    private PlantReqDto reqDto;

    @BeforeEach
    void setUp() {
        userId = 1L;

        // SecurityContext 에 사용자 설정
        UserDetailsImpl userDetails = new UserDetailsImpl(
                userId, "testUser", "test@example.com", "password",
                "city", "town", UserRole.USER, "", ""
        );
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );

        // 요청 데이터 초기화
        reqDto = new PlantReqDto("test plantName", 1L, LocalDate.of(2024, 1, 1));
    }

    @BeforeEach
    void initDatabase() {
        plantService.createPlant(userId, new PlantReqDto("test plantName", 1L, LocalDate.now()));
    }


    private static String serialize(Object object) throws JsonProcessingException {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void createPlant_Success() throws Exception {
        // when
        ResultActions result = mockMvc.perform(post("/api/v1/plants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(reqDto)));

        // then
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("식물이 등록되었습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void readPlant_Success() throws Exception {
        // when
        Long id = 1L;
        ResultActions result = mockMvc.perform(get("/api/v1/plants/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void updatePlant_Success() throws Exception {
        // when
        Long id = 1L;
        ResultActions result = mockMvc.perform(put("/api/v1/plants/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(serialize(reqDto)));

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("식물이 수정되었습니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void deletePlant_Success() throws Exception {
        // when
        Long id = 1L;
        ResultActions result = mockMvc.perform(delete("/api/v1/plants/{id}", id)
                .contentType(MediaType.APPLICATION_JSON));

        // then
        result.andExpect(status().isNoContent())
                .andDo(print());
    }
}
