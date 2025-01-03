package com.sounganization.botanify.domain.community.service;

import com.sounganization.botanify.domain.community.repository.ViewHistoryRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final ViewHistoryRepository viewHistoryRepository;
    private final EntityManager entityManager;

    @Scheduled(cron = "0 0 00 L * ?", zone = "Asia/Seoul")
    public void deleteAll() {
        viewHistoryRepository.deleteAll();
        entityManager.clear();
        log.info("viewHistory 삭제 스케줄러 동작");
    }
}
