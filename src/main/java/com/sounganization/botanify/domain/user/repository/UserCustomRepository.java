package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.enums.UserRole;
import java.util.Optional;

public interface UserCustomRepository {
    void updateUserInfo(Long id, String username, String password);
    void updateCoordinates(String nx, String ny, Long userId);
    Optional<UserRole> findRoleById(Long id);
    void updateAddressRoleAndCoordinates(Long id, String city, String town, String address, UserRole role, String nx, String ny);
}
