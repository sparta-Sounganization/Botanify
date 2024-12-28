package com.sounganization.botanify.domain.garden.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlantAlarm extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id", nullable = false)
    private Plant plant;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlarmType type;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private Integer intervalDays;

    @Column(nullable = false)
    private Boolean isEnabled;

    public enum AlarmType {
        WATER("물"),
        FERTILIZER("비료"),
        PESTICIDE("살충제");

        private final String description;

        AlarmType(String description) {
            this.description = description;
        }
    }

    public void update(LocalDate startDate, Integer intervalDays, Boolean isEnabled) {
        this.startDate = startDate;
        this.intervalDays = intervalDays;
        this.isEnabled = isEnabled;
    }

    public void addPlant(Plant plant) {
        this.plant = plant;
    }

    public void addUserId(Long userId) {
        this.userId = userId;
    }
}
