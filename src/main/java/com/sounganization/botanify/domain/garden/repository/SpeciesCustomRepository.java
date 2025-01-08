package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SpeciesCustomRepository {
    Page<Species> findBySearch(Pageable pageable, String search);

    Page<Species> findBySearch(Pageable pageable, String search, boolean sizeOnly);

    Species findByIdCustom(Long id);
}
