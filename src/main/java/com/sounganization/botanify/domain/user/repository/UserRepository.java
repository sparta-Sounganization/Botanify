package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.username FROM User u WHERE u.id IN :userIds")
    List<String> findUsernamesByIds(@Param("userIds") Set<Long> userIds);

    @Modifying
    @Query("UPDATE User u SET u.username = :username," +
            " u.password = :password, u.city = :city, u.town = :town, u.address = :address" +
            " WHERE u.id = :id")
    void updateUserInfo(@Param("id") Long id,
                        @Param("username") String username,
                        @Param("password") String password,
                        @Param("city") String city,
                        @Param("town") String town,
                        @Param("address") String address);
}
