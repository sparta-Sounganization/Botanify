package com.sounganization.botanify.common.service;

import com.sounganization.botanify.domain.community.entity.ViewHistory;
import com.sounganization.botanify.domain.community.repository.ViewHistoryRepository;
import com.sounganization.botanify.domain.garden.mapper.SpeciesMapper;
import com.sounganization.botanify.domain.garden.repository.SpeciesCacheRepository;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import com.sounganization.botanify.domain.plantApi.service.PlantApiAllService;
import jakarta.persistence.EntityManager;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SchedulerServiceTest {

    @Mock private EntityManager entityManager;
    @Mock private ViewHistoryRepository viewHistoryRepository;
    @Mock private PlantApiAllService plantApiAllService;
    @Mock private SpeciesRepository speciesRepository;
    @Mock private SpeciesMapper speciesMapper;
    @Mock private SpeciesCacheRepository speciesCacheRepository;

    private SchedulerService schedulerService;
    private AtomicBoolean taskExecuted;

    @BeforeEach
    void setUp() {
        //taskExecuted 플래그를 false로 초기화
        taskExecuted = new AtomicBoolean(false);

        //schedulerService 생성
        schedulerService = new SchedulerService(
                entityManager, viewHistoryRepository, plantApiAllService, speciesRepository, speciesMapper, speciesCacheRepository
        ) {
            @Override
            public void deleteAll() {
                super.deleteAll(); // 실제 deleteAll
                taskExecuted.set(true); // deleteAll 호출되면  taskExecuted를 true로 설정
            }
        };
    }

    @Test
    @DisplayName("deleteAll 메서드 호출 테스트")
    public void Schedulertest() {
        //deleteAll 메서드 별도의 스레드에서 실행
        new Thread(schedulerService::deleteAll).start();
        // Awaitility 사용해서 taskExecuted true 될 때 까지 기다림
        Awaitility.await()
                .atMost(5, TimeUnit.SECONDS)
                .untilTrue(taskExecuted);
        // deleteAll 메서드가 호출되었는지 확인
        verify(viewHistoryRepository, times(1)).deleteAll();
        verify(entityManager, times(1)).clear();
    }

    @Test
    @DisplayName("deleteAll 데이터 삭제 테스트")
    public void test() {
        //given
        ViewHistory viewHistory = ViewHistory.builder()
                .postId(1L)
                .userId(1L)
                .viewedAt(LocalDate.of(2024, 1, 1))
                .build();

        when(viewHistoryRepository.findAll())
                // 삭제 전 반환값
                .thenReturn(List.of(viewHistory))
                // 삭제 후 반환값
                .thenReturn(Collections.emptyList());

        //when
        //deleteAll 호출 전 데이터 확인
        List<ViewHistory> beforeDelete = viewHistoryRepository.findAll();
        assertEquals(1, beforeDelete.size(), "삭제 전 데이터는 1개여야 합니다.");

        //deleteAll 호출
        schedulerService.deleteAll();

        //then
        //deleteAll 호출 후 데이터 확인
        List<ViewHistory> afterDelete = viewHistoryRepository.findAll();
        assertEquals(0, afterDelete.size(), "삭제 후 데이터는 없어야 합니다.");

        //deleteAll 메서드 호출 확인
        verify(viewHistoryRepository, times(1)).deleteAll();
    }

}