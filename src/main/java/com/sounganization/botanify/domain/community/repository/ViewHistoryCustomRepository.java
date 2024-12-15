package com.sounganization.botanify.domain.community.repository;

import java.time.LocalDate;

public interface ViewHistoryCustomRepository {
    boolean existViewHistory( Long postId,Long userId,LocalDate viewedAt);
}
