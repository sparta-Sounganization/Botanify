package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.entity.User;
import com.sounganization.botanify.domain.user.projection.UserProjection;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserCustomRepository {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    //username 찾기 -> QueryDsl 로 변경했더니 post 단건조회 에러남
    @Query("SELECT u.id AS id, u.username AS username FROM User u WHERE u.id IN :userIds")
    List<UserProjection> findUsernamesByIds(@Param("userIds") List<Long> userIds);
}
