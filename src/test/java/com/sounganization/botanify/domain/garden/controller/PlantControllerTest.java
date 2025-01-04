//package com.sounganization.botanify.domain.garden.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.sounganization.botanify.BotanifyApplication;
//import com.sounganization.botanify.common.security.UserDetailsImpl;
//import com.sounganization.botanify.domain.garden.dto.req.PlantReqDto;
//import com.sounganization.botanify.domain.garden.entity.Plant;
//import com.sounganization.botanify.domain.garden.entity.Species;
//import com.sounganization.botanify.domain.garden.repository.PlantRepository;
//import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
//import com.sounganization.botanify.domain.garden.service.PlantService;
//import com.sounganization.botanify.domain.user.entity.User;
//import com.sounganization.botanify.domain.user.enums.UserRole;
//import com.sounganization.botanify.domain.user.repository.UserRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.TestPropertySource;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//import java.time.LocalDate;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//
//@SpringBootTest(
//        webEnvironment = SpringBootTest.WebEnvironment.MOCK,
//        classes = {BotanifyApplication.class}
//)
//@AutoConfigureMockMvc(addFilters = false)
//@ExtendWith(MockitoExtension.class)
//@ActiveProfiles("test")
//@Transactional
//@TestPropertySource(properties = {
//        "aws.s3.enabled=false",
//        "aws.s3.endpoint=http://localhost",
//        "aws.access-key=test-access-key",
//        "aws.secret-key=test-secret-key",
//        "species.api.base-url=http://test-species-api-url.com",
//        "species.api.key=test-species-api-key",
//        "spring.scheduling.enabled=false",
//        "nongsaro.api.base-url=http://test-nongsaro-api-url.com",
//        "nongsaro.api.key=test-nongsaro-api-key",
//        "kakao.api.key=test-kakao-api-key",
//        "aws.s3.bucket=test-bucket"
//})
//class PlantControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private PlantService plantService;
//
//    @Autowired
//    private EntityManager entityManager;
//
//    @Autowired
//    private PlantRepository plantRepository;
//
//    @Autowired
//    private SpeciesRepository speciesRepository;
//    @Autowired
//    private UserRepository userRepository;
//
//    private static final ObjectMapper objectMapper = new ObjectMapper()
//            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
//            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//    private Long userId;
//    private PlantReqDto reqDto;
//    private Species testSpecies;
//    JPAQueryFactory jpaQueryFactory;
//    private Plant testPlant;
//    private User testUser;
//
//
//
//    @BeforeEach
//    void setUp() {
//        userId = 1L;
//        jpaQueryFactory = new JPAQueryFactory(entityManager);
//
//        // 테스트 User 생성 및 저장
//        testUser = User.builder()
//                .username("testUser")
//                .email("test@example.com")
//                .password("password")
//                .city("city")
//                .town("town")
//                .role(UserRole.USER)
//                .address("address")
//                .build();
//        userRepository.save(testUser);
//        entityManager.refresh(testUser);
//
//        UserDetailsImpl userDetails = new UserDetailsImpl(
//                testUser.getId(), null, testUser.getUsername(), null,testUser.getCity(), testUser.getTown(),
//                testUser.getRole(), "", ""
//        );
//        SecurityContextHolder.getContext().setAuthentication(
//                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
//        );
//
//
//        // 기존 데이터 삭제
//        speciesRepository.deleteAll();
//
//        // 새로운 Species 데이터 생성 및 저장
//        testSpecies = Species.builder()
//                .plantName("test plantName")
//                .speciesName("Test Species")
//                .plantCode("12345")
//                .smell("None")
//                .toxicity("Low")
//                .managementLevel("Easy")
//                .growthSpeed("Fast")
//                .growthTemperature("20-25")
//                .winterLowestTemp("10")
//                .humidity("Moderate")
//                .fertilizerInfo("Monthly")
//                .waterSpring("Weekly")
//                .waterSummer("Twice a week")
//                .waterAutumn("Weekly")
//                .waterWinter("Bi-weekly")
//                .rtnFileUrl("http://example.com/image.jpg")
//                .build();
//
//        speciesRepository.save(testSpecies);
//        entityManager.refresh(testSpecies); // EntityManager로 최신 상태 동기화
//
//        reqDto = new PlantReqDto("test plantName", 1L, LocalDate.of(2024, 1, 1));
//
//        // Species 생성 및 저장 후
//        speciesRepository.save(testSpecies);
//        entityManager.refresh(testSpecies);
//
//        // Plant 엔티티 생성 및 저장
//        testPlant = Plant.builder()
//                .plantName("test plantName")
//                .species(testSpecies)
//                .adoptionDate(LocalDate.of(2024, 1, 1))
//                .userId(userId) // User 정보 추가
//                .build();
//        plantRepository.save(testPlant);
//        entityManager.refresh(testPlant);
//
//        reqDto = new PlantReqDto("test plantName", testSpecies.getId(), LocalDate.of(2024, 1, 1));
//    }
//
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    void createPlant_Success() throws Exception {
//        // 새로운 plant를 생성하는 요청
//        PlantReqDto newPlantDto = new PlantReqDto(
//                "new plantName",
//                testSpecies.getId(),
//                LocalDate.of(2024, 1, 1)
//        );
//        String reqDtoJson = objectMapper.writeValueAsString(newPlantDto);
//
//        ResultActions result = mockMvc.perform(post("/api/v1/plants")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(reqDtoJson));
//
//        result.andExpect(status().isCreated())
//                .andExpect(jsonPath("$.message").value("식물이 등록되었습니다."))
//                .andDo(print());
//    }
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    void readPlant_Success() throws Exception {
//        ResultActions result = mockMvc.perform(get("/api/v1/plants/{id}", testPlant.getId())
//                .contentType(MediaType.APPLICATION_JSON));
//
//        result.andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(testPlant.getId()))
//                .andExpect(jsonPath("$.plantName").value("test plantName"))
//                .andExpect(jsonPath("$.adoptionDate").value("2024-01-01"))
//                .andExpect(jsonPath("$.speciesName").value("Test Species"))
//                // diaries 필드 확인, 비어있는 content와 페이지 정보
//                .andExpect(jsonPath("$.diaries.content").isArray())
//                .andExpect(jsonPath("$.diaries.content").isEmpty())
//                .andExpect(jsonPath("$.diaries.page.size").value(10))
//                .andExpect(jsonPath("$.diaries.page.number").value(0))
//                .andExpect(jsonPath("$.diaries.page.totalElements").value(0))
//                .andExpect(jsonPath("$.diaries.page.totalPages").value(0))
//                .andDo(print());
//    }
//
//
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    void updatePlant_Success() throws Exception {
//        // 기존 plant를 업데이트하는 요청
//        PlantReqDto updateDto = new PlantReqDto(
//                "updated plantName",
//                testSpecies.getId(),
//                LocalDate.of(2024, 1, 2)
//        );
//        String reqDtoJson = objectMapper.writeValueAsString(updateDto);
//
//        ResultActions result = mockMvc.perform(put("/api/v1/plants/{id}", testPlant.getId())
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(reqDtoJson));
//
//        result.andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("식물이 수정되었습니다."))
//                .andDo(print());
//    }
//
//    @Test
//    @WithMockUser(username = "testUser", roles = {"USER"})
//    void deletePlant_Success() throws Exception {
//        ResultActions result = mockMvc.perform(delete("/api/v1/plants/{id}", 1L)
//                .contentType(MediaType.APPLICATION_JSON));
//
//        result.andExpect(status().isNoContent())
//                .andDo(print());
//    }
//}
