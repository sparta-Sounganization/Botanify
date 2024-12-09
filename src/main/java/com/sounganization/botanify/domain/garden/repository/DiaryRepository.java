package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.domain.garden.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Page<Diary> findByPlantId(Long id, Pageable pageable);
}