package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.service.SpeciesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpeciesController {
    private final SpeciesService speciesService;

    @PostMapping("/admin/species")
    public ResponseEntity<CommonResDto> createSpecies(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            HttpServletRequest httpReq,
            @Valid @RequestBody SpeciesReqDto reqDto
    ) {
        CommonResDto resDto = speciesService.createSpecies(userDetails.getRole(), reqDto);
        String createdUri = httpReq.getRequestURI() + "/" + resDto.id();
        return ResponseEntity.created(URI.create(createdUri)).body(resDto);
    }

    @GetMapping("/species")
    public ResponseEntity<?> getAllSpecies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(speciesService.readAllSpecies(page, size));
    }

    @GetMapping("/species/{id}")
    public ResponseEntity<SpeciesResDto> getSpecies(@PathVariable Long id) {
        return ResponseEntity.ok(speciesService.readSpecies(id));
    }

    @PutMapping("/species/{id}")
    public ResponseEntity<CommonResDto> updateSpecies(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id,
            @Valid @RequestBody SpeciesReqDto reqDto
    ) {
        return ResponseEntity.ok(speciesService.updateSpecies(userDetails.getRole(), id, reqDto));
    }

    @DeleteMapping("/species/{id}")
    public ResponseEntity<Void> deleteSpecies(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long id
    ) {
        speciesService.deleteSpecies(userDetails.getRole(), id);
        return ResponseEntity.noContent().build();
    }
}
