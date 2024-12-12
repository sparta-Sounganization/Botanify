package com.sounganization.botanify.domain.garden.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Builder
public class Plant extends Timestamped {
    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String plantName;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate adoptionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "species_id")
    private Species species;

    @Column(nullable = false)
    private Long userId;

    public void addRelations(Species species, Long userId) {
        this.species = species;
        this.userId = userId;
    }

    public void update(String plantName, LocalDate adoptionDate) {
        this.plantName = plantName;
        this.adoptionDate = adoptionDate;
    }
}
