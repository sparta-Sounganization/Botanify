package com.sounganization.botanify.domain.garden.entity;

import jakarta.persistence.*;

@Entity
public class Species {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    private String speciesName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;
}
