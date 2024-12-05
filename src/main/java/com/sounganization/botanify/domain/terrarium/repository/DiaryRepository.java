package com.sounganization.botanify.domain.terrarium.repository;

import com.sounganization.botanify.domain.terrarium.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
}
