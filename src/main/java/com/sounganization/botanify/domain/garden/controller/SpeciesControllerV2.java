package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.service.SpeciesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class SpeciesControllerV2 {
    private final SpeciesService speciesService;

    @GetMapping("/species")
    public ResponseEntity<Page<SpeciesResDto>> getAllSpecies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        return ResponseEntity.ok(speciesService.readAllSpeciesV2(page, size, search));
    }
}
