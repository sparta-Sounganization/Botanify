package com.sounganization.botanify.domain.garden.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
