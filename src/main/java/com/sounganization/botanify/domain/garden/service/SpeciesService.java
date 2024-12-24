package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.entity.Species;
import com.sounganization.botanify.domain.garden.mapper.SpeciesMapper;
import com.sounganization.botanify.domain.garden.repository.SpeciesRepository;
import com.sounganization.botanify.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SpeciesService {
    private final SpeciesRepository speciesRepository;
    private final SpeciesMapper speciesMapper;

    @Transactional
    public CommonResDto createSpecies(UserRole userRole, SpeciesReqDto reqDto) {

        if (!userRole.equals(UserRole.ADMIN)) throw new CustomException(ExceptionStatus.INVALID_ROLE);

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
    public CommonResDto updateSpecies(UserRole userRole, Long id, SpeciesReqDto reqDto) {

        if (!userRole.equals(UserRole.ADMIN)) throw new CustomException(ExceptionStatus.INVALID_ROLE);

        Species species = speciesRepository.findByIdCustom(id);

        species.update(reqDto.speciesName(), reqDto.description());

        return speciesMapper.toUpdatedDto(species.getId());
    }

    @Transactional
    public void deleteSpecies(UserRole userRole, Long speciesId) {

        if (!userRole.equals(UserRole.ADMIN)) throw new CustomException(ExceptionStatus.INVALID_ROLE);

        Species species = speciesRepository.findByIdCustom(speciesId);

        species.softDelete();
    }

}
