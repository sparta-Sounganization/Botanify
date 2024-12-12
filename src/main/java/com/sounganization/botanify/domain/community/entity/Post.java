package com.sounganization.botanify.domain.community.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@Entity
@AllArgsConstructor
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
    private Integer viewCounts;

    @Column(nullable = false)
    private Long userId;

    //조회수 증가
    public void incrementViewCounts() {
        viewCounts++;
    }

    //게시글 수정
    public void updatePost(String title, String content) {
        if (title != null) {
            this.title = title;
        }
        if (content != null) {
            this.content = content;
        }
    }

    //게시글 삭제
    public void softDelete() {
        super.softDelete();
    }

    //이미 삭제된 게시글인지 확인
    public boolean isDeletedYn() {
        return super.getDeletedYn();
    }
}
