package com.sounganization.botanify.domain.community.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;

@Entity
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer viewCounts;

    @Column(nullable = false)
    private Long userId;
}
