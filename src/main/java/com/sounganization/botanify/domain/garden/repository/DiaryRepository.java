package com.sounganization.botanify.domain.garden.repository;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.garden.entity.Diary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    Optional<Diary> findByIdAndDeletedYnFalse(Long id);
    default Diary findByIdCustom(Long id) {
        return findByIdAndDeletedYnFalse(id).orElseThrow(() -> new CustomException(ExceptionStatus.DIARY_NOT_FOUND));
    }
    Page<Diary> findAllByPlantIdAndDeletedYnFalse(Long plantId, Pageable pageable);
}
