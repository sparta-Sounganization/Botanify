package com.sounganization.botanify.domain.user.service;

import com.sounganization.botanify.common.dto.res.CommonResDto;
import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.common.util.JwtUtil;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import com.sounganization.botanify.domain.user.dto.req.AddressReqDto;
import com.sounganization.botanify.domain.user.dto.req.UserDeleteReqDto;
import com.sounganization.botanify.domain.user.dto.req.UserUpdateReqDto;
import com.sounganization.botanify.domain.user.dto.res.UserPlantsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserPostsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.mapper.UserMapper;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import com.sounganization.botanify.domain.weather.service.LocationService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PlantRepository plantRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final LocationService locationService;

    public UserResDto getUserInfo() {
        User user = getAuthenticatedUser();
        return userMapper.toResDto(user);
    }

    public UserPlantsResDto getUserInfoWithPlants(int page, int size) {
        User user = getAuthenticatedUser();
        Page<Plant> plants = getPlants(user.getId(), page, size);
        Page<PlantResDto> plantResDtos = mapPage(plants, this::toPlantResDto);
        UserResDto userResDto = userMapper.toResDto(user);
        return new UserPlantsResDto(userResDto, plantResDtos);
    }

    public UserPostsResDto getUserInfoWithPosts(int page, int size) {
        User user = getAuthenticatedUser();
        Page<Post> posts = getPosts(user.getId(), page, size);
        Page<PostListResDto> postResDtos = mapPage(posts, postMapper::entityToResDto);
        UserResDto userResDto = userMapper.toResDto(user);
        return new UserPostsResDto(userResDto, postResDtos);
    }

    @Transactional
    public CommonResDto updateUserInfo(UserUpdateReqDto updateReqDto) {
        User user = getAuthenticatedUser();
        validatePassword(updateReqDto.password(), user.toUserDetails().getPassword());
        String newPassword = getNewPassword(updateReqDto.newPassword(), user.toUserDetails().getPassword());
        userRepository.updateUserInfo(user.getId(),
                updateReqDto.username(),
                newPassword,
                updateReqDto.city(),
                updateReqDto.town(),
                updateReqDto.address());
        return new CommonResDto(HttpStatus.OK, "회원정보가 수정되었습니다.", user.getId());
    }

    @Transactional
    public void updateAddress(String token, AddressReqDto addressReqDto, HttpServletResponse response) {

        if (token == null || token.isEmpty()) {
            throw new CustomException(ExceptionStatus.TOKEN_NOT_PROVIDED);
        }

        try {
            String userId = jwtUtil.getClaimsFromToken(token).getSubject();

            String[] coordinates = locationService.getCoordinates(addressReqDto.city(), addressReqDto.town());
            String nx = coordinates != null ? coordinates[0] : null;
            String ny = coordinates != null ? coordinates[1] : null;

            UserRole currentRole = userRepository.findRoleById(Long.parseLong(userId))
                    .orElseThrow(() -> new CustomException(ExceptionStatus.USER_NOT_FOUND));
            UserRole newRole = currentRole == UserRole.GUEST ? UserRole.USER : currentRole;

            // 주소, Role, 좌표 동시 업데이트
            userRepository.updateAddressRoleAndCoordinates(
                    Long.parseLong(userId),
                    addressReqDto.city(),
                    addressReqDto.town(),
                    addressReqDto.address(),
                    newRole,
                    nx,
                    ny
            );

            // JWT 재발급
            String newToken = jwtUtil.generateToken(
                    userId,
                    jwtUtil.getCurrentUsername(token),
                    newRole,
                    addressReqDto.city(),
                    addressReqDto.town(),
                    nx,
                    ny
            );

            jwtUtil.addJwtToCookie(newToken, response);

        } catch (Exception e) {
            throw new CustomException(ExceptionStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public void deleteUser(UserDeleteReqDto userDeleteReqDto) {
        User user = getAuthenticatedUser();
        validatePassword(userDeleteReqDto.password(), user.toUserDetails().getPassword());
        user.softDelete();
        userRepository.save(user);
    }

    private User getAuthenticatedUser() {
        String userIdStr = jwtUtil.getCurrentUserId();
        Long userId = Long.parseLong(userIdStr);
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_DETAILS_NOT_FOUND));
    }

    private Pageable createPageable(int page, int size) {
        return PageRequest.of(page - 1, size);
    }

    private <T, R> Page<R> mapPage(Page<T> source, Function<T, R> mapper) {
        return source.map(mapper);
    }

    private Page<Plant> getPlants(Long userId, int page, int size) {
        Pageable pageable = createPageable(page, size);
        return plantRepository.findAllByUserIdAndDeletedYnFalse(userId, pageable);
    }

    private Page<Post> getPosts(Long userId, int page, int size) {
        Pageable pageable = createPageable(page, size);
        return postRepository.findAllByUserIdAndDeletedYnFalse(userId, pageable);
    }

    private PlantResDto toPlantResDto(Plant plant) {
        return PlantResDto.builder()
                .id(plant.getId())
                .plantName(plant.getPlantName())
                .adoptionDate(plant.getAdoptionDate())
                .speciesName(plant.getSpecies().getSpeciesName())
                .build();
    }

    private void validatePassword(String inputPassword, String storedPassword) {
        if (!passwordEncoder.matches(inputPassword, storedPassword)) {
            throw new CustomException(ExceptionStatus.INVALID_PASSWORD);
        }
    }

    private String getNewPassword(String newPassword, String currentPassword) {
        return newPassword != null ? passwordEncoder.encode(newPassword) : currentPassword;
    }
}
