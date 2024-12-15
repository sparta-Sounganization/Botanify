package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long>, ViewHistoryCustomRepository {
}
