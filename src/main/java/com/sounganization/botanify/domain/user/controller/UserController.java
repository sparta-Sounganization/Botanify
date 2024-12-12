package com.sounganization.botanify.domain.user.controller;

import com.sounganization.botanify.domain.user.dto.res.UserPlantsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserPostsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import com.sounganization.botanify.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResDto> getUserInfo() {
        return ResponseEntity.ok(userService.getUserInfo());
    }

    @GetMapping("/me/plants")
    public ResponseEntity<UserPlantsResDto> getUserInfoWithDiaries(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserPlantsResDto userInfoWithDiaries = userService.getUserInfoWithDiaries(page, size);
        return ResponseEntity.ok(userInfoWithDiaries);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<UserPostsResDto> getUserInfoWithPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserPostsResDto userInfoWithPosts = userService.getUserInfoWithPosts(page, size);
        return ResponseEntity.ok(userInfoWithPosts);
    }
}
