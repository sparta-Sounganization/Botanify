package com.sounganization.botanify.domain.admin.controller;

import com.sounganization.botanify.domain.admin.service.AdminService;
import com.sounganization.botanify.domain.user.dto.res.UserPlantsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserPostsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResDto> getUserProfile(@PathVariable Long userId) {
        UserResDto response = adminService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/plants")
    public ResponseEntity<UserPlantsResDto> getUserProfileWithPlants(@PathVariable Long userId,
                                                                     @RequestParam int plantPage,
                                                                     @RequestParam int plantSize,
                                                                     @RequestParam int diaryPage,
                                                                     @RequestParam int diarySize) {
        UserPlantsResDto response = adminService.getUserProfileWithPlants(userId, plantPage, plantSize, diaryPage, diarySize);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<UserPostsResDto> getUserProfileWithPosts(@PathVariable Long userId,
                                                                   @RequestParam int page,
                                                                   @RequestParam int size) {
        UserPostsResDto response = adminService.getUserProfileWithPosts(userId, page, size);
        return ResponseEntity.ok(response);
    }
}
