package com.sounganization.botanify.domain.admin.service;

import com.sounganization.botanify.common.exception.CustomException;
import com.sounganization.botanify.common.exception.ExceptionStatus;
import com.sounganization.botanify.domain.community.dto.res.PostListResDto;
import com.sounganization.botanify.domain.community.entity.Post;
import com.sounganization.botanify.domain.community.mapper.PostMapper;
import com.sounganization.botanify.domain.community.repository.PostRepository;
import com.sounganization.botanify.domain.garden.dto.res.DiaryResDto;
import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
import com.sounganization.botanify.domain.garden.entity.Plant;
import com.sounganization.botanify.domain.garden.mapper.DiaryMapper;
import com.sounganization.botanify.domain.garden.repository.DiaryRepository;
import com.sounganization.botanify.domain.garden.repository.PlantRepository;
import com.sounganization.botanify.domain.user.dto.res.UserPlantsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserPostsResDto;
import com.sounganization.botanify.domain.user.dto.res.UserResDto;
import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.mapper.UserMapper;
import com.sounganization.botanify.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PlantRepository plantRepository;
    private final DiaryRepository diaryRepository;
    private final DiaryMapper diaryMapper;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    public UserResDto getUserProfile(Long userId) {
        User user = getUserById(userId);
        return userMapper.toResDto(user);
    }

    public UserPlantsResDto getUserProfileWithPlants(Long userId, int plantPage, int plantSize, int diaryPage, int diarySize) {
        User user = getUserById(userId);
        Pageable plantPageable = PageRequest.of(plantPage - 1, plantSize);
        Pageable diaryPageable = PageRequest.of(diaryPage - 1, diarySize);
        Page<Plant> plants = plantRepository.findAllByUserIdAndDeletedYnFalse(user.getId(), plantPageable);

        Page<PlantResDto> plantResDtos = getPlantResDtos(plants, diaryPageable);

        UserResDto userResDto = userMapper.toResDto(user);
        return new UserPlantsResDto(userResDto, plantResDtos);
    }

    public UserPostsResDto getUserProfileWithPosts(Long userId, int page, int size) {
        User user = getUserById(userId);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Post> posts = postRepository.findAllByUserIdAndDeletedYnFalse(user.getId(), pageable);

        Page<PostListResDto> postResDtos = posts.map(postMapper::entityToResDto);
        UserResDto userResDto = userMapper.toResDto(user);
        return new UserPostsResDto(userResDto, postResDtos);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ExceptionStatus.USER_DETAILS_NOT_FOUND));
    }

    private Page<PlantResDto> getPlantResDtos(Page<Plant> plants, Pageable diaryPageable) {
        return plants.map(plant -> {
            List<DiaryResDto> diaryResDtos = getDiaryResDtos(plant, diaryPageable);
            return new PlantResDto(plant.getId(),
                    plant.getPlantName(),
                    plant.getAdoptionDate(),
                    plant.getSpecies().getSpeciesName(),
                    new PageImpl<>(diaryResDtos, diaryPageable, diaryResDtos.size()));
        });
    }

    private List<DiaryResDto> getDiaryResDtos(Plant plant, Pageable pageable) {
        return diaryRepository.findAllByPlantIdAndDeletedYnFalse(plant.getId(), pageable)
                .stream()
                .map(diaryMapper::toDto)
                .collect(Collectors.toList());
    }
}
