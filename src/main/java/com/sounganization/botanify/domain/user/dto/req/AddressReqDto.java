package com.sounganization.botanify.domain.user.dto.req;

import jakarta.validation.constraints.NotBlank;

public record AddressReqDto(
        @NotBlank(message = "도시를 입력해주세요.")
        String city,
        @NotBlank(message = "읍/면/동을 입력해주세요.")
        String town,
        @NotBlank(message = "상세 주소를 입력해주세요.")
        String address
) {}
