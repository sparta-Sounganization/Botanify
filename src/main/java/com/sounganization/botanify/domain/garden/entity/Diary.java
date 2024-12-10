package com.sounganization.botanify.domain.garden.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Diary extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plant_id")
    private Plant plant;

    @Column(nullable = false)
    private Long userId;

    public void addRelations(Plant plant, Long userId) {
        this.plant = plant;
        this.userId = userId;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
