package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesDetailResDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.mapper.SpeciesMapper;
import com.sounganization.botanify.domain.garden.repository.SpeciesCacheRepository;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import com.sounganization.botanify.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class SpeciesService {
    private final SpeciesRepository speciesRepository;
    private final SpeciesMapper speciesMapper;
    private final SpeciesCacheRepository speciesCacheRepository;

    @Transactional
    public CommonResDto createSpecies(UserRole userRole, SpeciesReqDto reqDto) {
        if (!userRole.equals(UserRole.ADMIN)) throw new CustomException(ExceptionStatus.INVALID_ROLE);
        Species reqSpecies = speciesMapper.toEntity(reqDto);
        Species resSpecies = speciesRepository.save(reqSpecies);
        speciesCacheRepository.overwrite(speciesMapper.toDetailDto(resSpecies));   // 생성 시 캐싱 추가
        return speciesMapper.toCreatedDto(resSpecies.getId());
    }

    public Page<SpeciesResDto> readAllSpecies(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Species> speciesList = speciesRepository.findBySearch(pageable, search);
        return speciesList.map(speciesMapper::toDto);
    }

    public Page<SpeciesResDto> readAllSpeciesV2(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<SpeciesDetailResDto> detailList = speciesCacheRepository.findAllFromCache(pageable, search);

//        주석 삭제 금지 (캐시 신뢰도 검사 로직)
//        // 실제 DB 와 사이즈만 비교하여 캐시 신뢰도를 강화
//        Long actualTotal = speciesRepository.findBySearch(pageable, search, true).getTotalElements();
//        Long cachedTotal = detailList.getTotalElements();
//        // 사이즈가 다를 경우, 캐시 최신화 및 실제 DB 조회 결과 반환
//        if (!cachedTotal.equals(actualTotal)) {
//            speciesCacheRepository.overwriteAll(speciesRepository.findAll().stream().map(speciesMapper::toDetailDto).toList());
//            return speciesRepository.findBySearch(pageable, search).map(speciesMapper::toDto);
//        }

        // 단건 조회용 DTO 리스트를 기존 전체 조회용 DTO 리스트로 변환
        return detailList.map(speciesMapper::toDto);
    }

    public SpeciesDetailResDto readSpecies(Long id) {
        // 단건 조회 시 캐시 먼저 조회하도록 수정
        SpeciesDetailResDto resDto = speciesCacheRepository.findFromCache(id).orElse(null);
        if (Objects.isNull(resDto)) {
            resDto = speciesMapper.toDetailDto(speciesRepository.findByIdCustom(id));
            speciesCacheRepository.overwrite(resDto);
        }
        return resDto;
    }

    @Transactional
    public CommonResDto updateSpecies(UserRole userRole, Long id, SpeciesReqDto reqDto) {
        if (!userRole.equals(UserRole.ADMIN)) throw new CustomException(ExceptionStatus.INVALID_ROLE);
        Species species = speciesRepository.findByIdCustom(id);
        species.update(reqDto.plantName());
        speciesCacheRepository.overwrite(speciesMapper.toDetailDto(species));  // 수정 시 캐싱 추가
        return speciesMapper.toUpdatedDto(species.getId());
    }

    @Transactional
    public void deleteSpecies(UserRole userRole, Long id) {
        if (!userRole.equals(UserRole.ADMIN)) throw new CustomException(ExceptionStatus.INVALID_ROLE);
        Species species = speciesRepository.findByIdCustom(id);
        species.softDelete();
        speciesCacheRepository.delete(id);  // 삭제 시 캐시 삭제 추가
    }

}
