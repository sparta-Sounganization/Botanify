package com.sounganization.botanify.domain.user.dto.res;

import com.sounganization.botanify.domain.garden.dto.res.PlantResDto;
import org.springframework.data.domain.Page;

public record UserPlantsResDto(
        UserResDto userInfo,
        Page<PlantResDto> plants) {}
