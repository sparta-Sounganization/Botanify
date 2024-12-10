package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.mapper.SpeciesMapper;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
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

    @Transactional
    public MessageResDto createSpecies(Long userId, SpeciesReqDto reqDto) {

        // todo - 사용자 존재 및 권한이 관리자인지 확인
        if (Objects.nonNull(userId)) throw new CustomException(ExceptionStatus.INVALID_ROLE);

        Species reqSpecies = speciesMapper.toEntity(reqDto);

        Species resSpecies = speciesRepository.save(reqSpecies);

        return speciesMapper.toCreatedDto(resSpecies.getId());
    }

    public Page<SpeciesResDto> readAllSpecies(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Species> speciesList = speciesRepository.findAllByDeletedYnFalse(pageable);

        return speciesList.map(speciesMapper::toDto);
    }

    public SpeciesResDto readSpecies(Long id) {

        Species species = speciesRepository.findByIdCustom(id);

        return speciesMapper.toDto(species);
    }

    @Transactional
    public MessageResDto updateSpecies(Long userId, Long id, SpeciesReqDto reqDto) {

        // todo - 사용자 존재 및 권한이 관리자인지 확인
        if (Objects.nonNull(userId)) throw new CustomException(ExceptionStatus.INVALID_ROLE);

        Species species = speciesRepository.findByIdCustom(id);

        species.update(reqDto.speciesName(), reqDto.description());

        return speciesMapper.toUpdatedDto(species.getId());
    }

    @Transactional
    public void deleteSpecies(Long userId, Long speciesId) {

        // todo - 사용자 존재 및 권한이 관리자인지 확인
        if (Objects.nonNull(userId)) throw new CustomException(ExceptionStatus.INVALID_ROLE);

        Species species = speciesRepository.findByIdCustom(speciesId);

        species.softDelete();
    }

}
