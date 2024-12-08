package com.sounganization.botanify.domain.community.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {
    private List<T> content; // 실제 데이터
    private int pageNumber; // 현재 페이지
    private int pageSize; // 페이지 크기
    private long totalElements; // 총 데이터 개수
    private int totalPages; // 총 페이지 수
    private boolean last; // 마지막 페이지 여부

    public PageDto(Page<T> page) {
        this.content = page.getContent();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}
