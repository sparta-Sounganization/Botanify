package com.sounganization.botanify.domain.terrarium.service;

import com.sounganization.botanify.domain.terrarium.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryService {
    private final DiaryRepository diaryRepository;


}
