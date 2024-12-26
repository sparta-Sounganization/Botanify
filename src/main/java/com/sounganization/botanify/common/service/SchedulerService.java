package com.sounganization.botanify.common.service;

import com.sounganization.botanify.domain.community.repository.ViewHistoryRepository;
import com.sounganization.botanify.domain.garden.mapper.SpeciesMapper;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import com.sounganization.botanify.domain.plantApi.service.PlantApiAllService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final EntityManager entityManager;
    private final ViewHistoryRepository viewHistoryRepository;
    private final PlantApiAllService plantApiAllService;
    private final SpeciesRepository speciesRepository;
    private final SpeciesMapper speciesMapper;

    @Scheduled(cron = "0 0 00 L * ?", zone = "Asia/Seoul")
    public void deleteAll() {
        viewHistoryRepository.deleteAll();
        entityManager.clear();
        log.info("viewHistory 삭제 스케줄러 동작");
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    public Mono<Void> updateSpeciesTableFromAPI() {
        return plantApiAllService.getSpeciesWithDetails()
                .switchIfEmpty(Mono.just(Collections.emptyList()))
                .flatMapMany(speciesList -> {
                    int chunkSize = 100;
                    int totalSize = speciesList.size();
                    // 100개씩 분할하여 처리
                    return Flux.range(0, (totalSize + chunkSize - 1) / chunkSize)
                            .map(chunkIndex -> {
                                int start = chunkIndex * chunkSize;
                                int end = Math.min(start + chunkSize, totalSize);
                                return speciesList.subList(start, end)
                                        .stream()
                                        .map(speciesMapper::toEntity)
                                        .toList();
                            })
                            .map(speciesRepository::saveAll); // 테이블에 저장
                })
                .then(Mono.fromRunnable(() -> log.info("식물 API 호출 및 Species 테이블 갱신 완료")));
    }

}
