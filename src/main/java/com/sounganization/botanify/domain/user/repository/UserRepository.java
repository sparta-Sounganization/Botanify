package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.username FROM User u WHERE u.id IN :userIds")
    List<String> findUsernamesByIds(@Param("userIds") Set<Long> userIds);
}
