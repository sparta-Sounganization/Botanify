package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}
