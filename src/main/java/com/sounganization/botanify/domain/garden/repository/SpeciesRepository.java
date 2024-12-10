package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpeciesRepository extends JpaRepository<Species, Long> {
    Optional<Species> findByIdAndDeletedYnFalse(Long id);
}
