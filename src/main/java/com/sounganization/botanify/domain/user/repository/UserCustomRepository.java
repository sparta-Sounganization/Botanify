package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.projection.UserProjection;

import java.util.List;
import java.util.Optional;

public interface UserCustomRepository {
    List<UserProjection> findUsernamesByIds(List<Long> userIds);
    void updateUserInfo(Long id, String username, String password, String city, String town, String address);
    void updateCoordinates(String nx, String ny, Long userId);
    Optional<UserRole> findRoleById(Long id);
    void updateAddressRoleAndCoordinates(Long id, String city, String town, String address, UserRole role, String nx, String ny);
}
