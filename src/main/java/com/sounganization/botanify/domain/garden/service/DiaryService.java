package com.sounganization.botanify.domain.garden.service;

import com.sounganization.botanify.domain.garden.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;


}
