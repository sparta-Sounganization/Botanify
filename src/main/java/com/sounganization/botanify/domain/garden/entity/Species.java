package com.sounganization.botanify.domain.garden.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Species extends Timestamped {
    @Id @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    // 필수 값
    @Column(nullable = false, length = 50) private String plantName; // cntntsSj

    // API 에 의한 동적 값 (description)
    @Column private String speciesName; // codeNm
    @Column private String plantCode; // cntntsNo
    @Column private String smell;
    @Column private String toxicity;
    @Column private String managementLevel;
    @Column private String growthSpeed;
    @Column private String growthTemperature;
    @Column private String winterLowestTemp;
    @Column private String humidity;
    @Column private String fertilizerInfo;
    @Column private String waterSpring;
    @Column private String waterSummer;
    @Column private String waterAutumn;
    @Column private String waterWinter;
    @Column private String rtnFileUrl;

    public void update(String plantName) {
        if (Objects.nonNull(plantName)) {
            this.plantName = plantName;
        }
    }
}
