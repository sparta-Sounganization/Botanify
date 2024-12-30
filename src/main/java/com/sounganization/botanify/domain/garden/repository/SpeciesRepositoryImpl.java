package com.sounganization.botanify.domain.garden.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.entity.QSpecies;
import com.sounganization.botanify.domain.garden.entity.Species;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class SpeciesRepositoryImpl implements SpeciesCustomRepository {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Species> findBySearch(Pageable pageable, String search) {
        QSpecies species = QSpecies.species;

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(species.deletedYn.isFalse());
        if (search != null && !search.isEmpty()) {
            whereClause.and(
                    species.plantName.likeIgnoreCase("%" + search + "%")
                            .or(species.speciesName.likeIgnoreCase("%" + search + "%"))
            );
        }

        List<Species> results = jpaQueryFactory.selectFrom(species)
                .where(whereClause)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(species.plantName.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(results, pageable, results.size());
    }

    @Override
    public Species findByIdCustom(Long id) {
        QSpecies species = QSpecies.species;
        Species foundSpecies = jpaQueryFactory
                .selectFrom(species)
                .where(species.id.eq(id).and(species.deletedYn.eq(false)))
                .fetchOne();

        if (foundSpecies == null) {
            throw new CustomException(ExceptionStatus.SPECIES_NOT_FOUND);
        }
        return foundSpecies;
    }
}
