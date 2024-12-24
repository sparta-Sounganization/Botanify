package com.sounganization.botanify.domain.garden.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Species extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    //품종 코드
    @Column(nullable = false)
    private String speciesCode;

    //품종 이름
    @Column(nullable = false, length = 50)
    private String speciesName;

    //식물코드
    @Column(nullable = false)
    private String plantCode;

    //식물이름
    @Column(nullable = false)
    private String plantName;

    //설명
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    public void update(String speciesName, String description) {
        this.speciesName = speciesName;
        this.description = description;
    }
}