package com.sounganization.botanify.domain.user.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.user.entity.QUser;
import com.sounganization.botanify.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;

    @Override
    public void updateUserInfo(Long id, String username, String password) {
        jpaQueryFactory.update(user)
                .set(user.username, username)
                .set(user.password, password)
                .where(user.id.eq(id))
                .execute();
    }

    @Override
    public void updateCoordinates(String nx, String ny, Long userId) {
        jpaQueryFactory.update(user)
                .set(user.nx, nx)
                .set(user.ny, ny)
                .where(user.id.eq(userId))
                .execute();
    }

    @Override
    public Optional<UserRole> findRoleById(Long userId) {
        QUser user = QUser.user;

        UserRole role = jpaQueryFactory.select(user.role)
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();

        return Optional.ofNullable(role);
    }

    @Override
    public void updateAddressRoleAndCoordinates(
            Long id,
            String city,
            String town,
            String address,
            UserRole role,
            String nx, String ny) {
        jpaQueryFactory.update(user)
                .set(user.city, city)
                .set(user.town, town)
                .set(user.address, address)
                .set(user.role, role)
                .set(user.nx, nx)
                .set(user.ny, ny)
                .where(user.id.eq(id))
                .execute();
    }
}
