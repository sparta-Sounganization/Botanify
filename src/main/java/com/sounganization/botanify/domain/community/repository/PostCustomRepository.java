package com.sounganization.botanify.domain.community.repository;

import com.sounganization.botanify.domain.community.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface PostCustomRepository {
    Page<Post> findAllByDetailedQuery(
            Pageable pageable,
            String sortBy,
            String order,
            String city,
            String town,
            String search,
            LocalDate dateBefore
    );
}
