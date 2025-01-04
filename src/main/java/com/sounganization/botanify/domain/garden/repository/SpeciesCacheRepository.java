package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.dto.res.SpeciesDetailResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SpeciesCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String SPECIES_KEY = "species:table";

    // 전부 지우고 새로 넣음 (트랜잭션 없으면 삭제만 수행되고 종료될 가능성이 있으므로 주의)
    @Transactional
    public void overwriteAll(List<SpeciesDetailResDto> speciesList) {
        redisTemplate.delete(SPECIES_KEY);

        Map<String, SpeciesDetailResDto> speciesMap = speciesList.stream()
                .collect(Collectors.toMap(s -> String.valueOf(s.id()), s -> s));

        redisTemplate.opsForHash().putAll(SPECIES_KEY, speciesMap);
    }

    // 있으면 덮어씀
    @Transactional
    public void overwrite(SpeciesDetailResDto resDto) {
        redisTemplate.opsForHash().put(SPECIES_KEY, String.valueOf(resDto.id()), resDto);
    }

    // 전부 반환함
    public Page<SpeciesDetailResDto> findAllFromCache(Pageable pageable, String search) {
        List<SpeciesDetailResDto> allItems = redisTemplate.opsForHash().entries(SPECIES_KEY)
                .values().stream()
                .map(s -> (SpeciesDetailResDto) s)
                .filter(dto -> Objects.isNull(search) || dto.cntntsSj().contains(search) || dto.codeNm().contains(search)) // search 조건 필터링
                .sorted(Comparator.comparing(SpeciesDetailResDto::cntntsSj))
                .toList();

        // 페이징 처리
        int start = Math.min(pageable.getPageNumber() * pageable.getPageSize(), allItems.size());
        int end = Math.min(start + pageable.getPageSize(), allItems.size());
        List<SpeciesDetailResDto> pagedItems = allItems.subList(start, end);

        // Page 객체로 반환
        return new PageImpl<>(pagedItems, pageable, allItems.size());
    }


    // 있으면 반환함
    public Optional<SpeciesDetailResDto> findFromCache(Long id) {
        SpeciesDetailResDto resDto = (SpeciesDetailResDto) redisTemplate.opsForHash().get(SPECIES_KEY, String.valueOf(id));
        return Optional.ofNullable(resDto);
    }

    // 전부 지움
    @Transactional
    public void deleteAll() {
        redisTemplate.delete(SPECIES_KEY);
    }

    // 있으면 지움
    @Transactional
    public void delete(Long id) {
        redisTemplate.opsForHash().delete(SPECIES_KEY, String.valueOf(id));
    }
}
