package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long>, ViewHistoryCustomRepository {
    @Modifying
    @Transactional
    @Query("DELETE FROM ViewHistory v")
    void deleteAll();
}
