package com.sounganization.botanify.domain.user.repository;

import com.sounganization.botanify.domain.user.dto.req.UserReqDto;
import com.sounganization.botanify.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    default Optional<UserReqDto> findByEmail(String email) {
        return findUserEntityByEmail(email).map(User::toDto);
    }

    Optional<User> findUserEntityByEmail(String email);


    default User saveNewUser(UserReqDto dto) {
        User newUser = new User(
                dto.email(),
                dto.username(),
                dto.password(),
                dto.role(),
                dto.city(),
                dto.town(),
                dto.address()
        );

        return save(newUser);
    }

    //username 찾기
    @Query("SELECT u.username FROM User u WHERE u.id IN :userIds")
    List<String> findUsernamesByIds(@Param("userIds") Set<Long> userIds);
}
