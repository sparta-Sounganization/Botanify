package com.sounganization.botanify.domain.garden.controller;

import com.sounganization.botanify.domain.garden.dto.req.SpeciesReqDto;
import com.sounganization.botanify.domain.garden.dto.res.MessageResDto;
import com.sounganization.botanify.domain.garden.dto.res.SpeciesResDto;
import com.sounganization.botanify.domain.garden.service.SpeciesService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SpeciesController {
    private final SpeciesService speciesService;

    @PostMapping("/admin/species")
    public ResponseEntity<MessageResDto> createSpecies(
            @RequestParam Long userId,
            HttpServletRequest httpReq,
            @Valid @RequestBody SpeciesReqDto reqDto
    ) {
        MessageResDto resDto = speciesService.createSpecies(userId, reqDto);
        String createdUri = httpReq.getRequestURI() + "/" + resDto.id();
        return ResponseEntity.created(URI.create(createdUri)).body(resDto);
    }

    @GetMapping
    public ResponseEntity<?> getAllSpecies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(speciesService.readAllSpecies(page, size));
    }

    @GetMapping("{id}")
    public ResponseEntity<SpeciesResDto> getSpecies(@PathVariable Long id) {
        return ResponseEntity.ok(speciesService.readSpecies(id));
    }

    @PutMapping("{id}")
    public ResponseEntity<MessageResDto> updateSpecies(
            @RequestParam Long userId,
            @PathVariable Long id,
            @Valid @RequestBody SpeciesReqDto reqDto
    ) {
        return ResponseEntity.ok(speciesService.updateSpecies(userId, id, reqDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteSpecies(@RequestParam Long userId, @PathVariable Long id) {
        speciesService.deleteSpecies(userId, id);
        return ResponseEntity.noContent().build();
    }
}
