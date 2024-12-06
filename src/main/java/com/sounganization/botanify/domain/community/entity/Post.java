package com.sounganization.botanify.domain.community.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Post extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)

    private Integer viewCounts=0;  //조회수 기본값 0 설정

    @Column(nullable = false)
    private Long userId;

    public Post(String titile, String content, Long userId) {
        this.title = titile;
        this.content = content;
        this.userId = userId;
    }
}
