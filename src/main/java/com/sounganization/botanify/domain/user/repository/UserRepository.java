package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.projection.UserProjection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    //username 찾기
    @Query("SELECT u.id AS id, u.username AS username FROM User u WHERE u.id IN :userIds")
    List<UserProjection> findUsernamesByIds(@Param("userIds") List<Long> userIds);

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
