package com.sounganization.botanify.domain.user.controller;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.domain.user.dto.req.AddressReqDto;
import com.sounganization.botanify.domain.user.dto.req.UserDeleteReqDto;
import com.sounganization.botanify.domain.user.dto.req.UserUpdateReqDto;
import com.sounganization.botanify.domain.user.dto.res.UserPlantsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserPostsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import com.sounganization.botanify.domain.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<UserPlantsResDto> getUserInfoWithPlants(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserPlantsResDto userInfoWithPlants = userService.getUserInfoWithPlants(page, size);
        return ResponseEntity.ok(userInfoWithPlants);
    }

    @GetMapping("/me/posts")
    public ResponseEntity<UserPostsResDto> getUserInfoWithPosts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserPostsResDto userInfoWithPosts = userService.getUserInfoWithPosts(page, size);
        return ResponseEntity.ok(userInfoWithPosts);
    }

    @PutMapping("/me")
    public ResponseEntity<CommonResDto> updateUserInfo(@Valid @RequestBody UserUpdateReqDto userUpdateReqDto) {
        CommonResDto response = userService.updateUserInfo(userUpdateReqDto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/address")
    public ResponseEntity<CommonResDto> updateAddress(
            @CookieValue(value = "Authorization") String token,
            @Valid @RequestBody AddressReqDto addressReqDto,
            HttpServletResponse response) {

        userService.updateAddress(token, addressReqDto, response);
        return ResponseEntity.ok(new CommonResDto(HttpStatus.OK, "주소가 업데이트되었습니다."));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody
                                           UserDeleteReqDto userDeleteReqDto,
                                           HttpServletResponse response) {
        userService.deleteUser(userDeleteReqDto);

        Cookie cookie = new Cookie("Authorization", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();
    }
}
