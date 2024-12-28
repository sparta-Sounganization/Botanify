package com.sounganization.botanify.common.service;

import com.sounganization.botanify.domain.community.repository.ViewHistoryRepository;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.mapper.SpeciesMapper;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import com.sounganization.botanify.domain.plantApi.service.PlantApiAllService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j(topic = "스케줄러 서비스")
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
        // API 호출로 전체 파이프라인을 시작합니다.
        return plantApiAllService.getSpeciesWithDetails()
                .switchIfEmpty(Mono.just(Collections.emptyList()))
                .publishOn(Schedulers.boundedElastic())
                // Mono 타입에서의 map 메서드는 단일 항목 대상으로 적용되며, 동기 작업을 정의할 수 있습니다.
                .map(responses -> {

                    // 응답 결과를 새로운 엔티티 리스트로 만듭니다. saveAll 호출 시, id 가 지정된 데이터만, 동일 id 기존 위치에 UPDATE 됩니다.
                    List<Species> list = responses.stream().map(speciesMapper::toEntity).toList();
                    // 저장 예정인 객체를 plantCode 를 이용하여 찾기 위한 참조 맵입니다.
                    Map<String, Species> map = list.stream().collect(Collectors.toMap(Species::getPlantCode, e -> e));

                    // 응답에 존재하는 plantCode 와 실제 데이터의 교집합을 구합니다.
                    List<String> responsePlantCodes = list.stream().map(Species::getPlantCode).toList();
                    List<Species> presents = speciesRepository.findAllByPlantCodeInAndDeletedYnFalse(responsePlantCodes);
                    // 교집합에 대해서만 UPDATE 할 수 있도록 참조 맵을 이용하여 id 를 지정해 줍니다.
                    presents.forEach(species -> map.get(species.getPlantCode()).setId(species.getId()));

                    // saveAll 을 호출하여 있는 값 UPDATE, 추가된 값 INSERT, 로그 출력을 수행합니다.
                    speciesRepository.saveAll(list);
                    log.info("식물 API 품종 - 추가:{}, 갱신:{}", list.size() - presents.size(), presents.size());
                    return responses;
                })
                .then(Mono.fromRunnable(() -> log.info("식물 API 호출 및 Species 테이블 갱신 완료")));
    }

}
