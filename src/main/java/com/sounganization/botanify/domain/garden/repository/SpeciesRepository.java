package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.Species;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpeciesRepository extends JpaRepository<Species, Long>, SpeciesCustomRepository {
//    Page<Species> findAllByDeletedYnFalse(Pageable pageable);

//    Optional<Species> findByIdAndDeletedYnFalse(Long id);

//    default Species findByIdCustom(Long id) {
//        return findByIdAndDeletedYnFalse(id).orElseThrow(()-> new CustomException(ExceptionStatus.SPECIES_NOT_FOUND));
//    }
}
