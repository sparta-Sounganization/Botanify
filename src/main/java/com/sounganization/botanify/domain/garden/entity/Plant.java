package com.sounganization.botanify.domain.garden.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class Plant extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String plantName;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate adoptionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(nullable = false)
    private Long userId;

}
