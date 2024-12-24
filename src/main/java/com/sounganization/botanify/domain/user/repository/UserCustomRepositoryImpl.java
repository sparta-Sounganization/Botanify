package com.sounganization.botanify.domain.user.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sounganization.botanify.domain.user.entity.QUser;
import com.sounganization.botanify.domain.user.enums.UserRole;
import com.sounganization.botanify.domain.user.projection.UserProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final QUser user = QUser.user;

    @Override
    public List<UserProjection> findUsernamesByIds(List<Long> userIds) {
        return jpaQueryFactory.select(Projections.fields(UserProjection.class,
                user.id.as("id"),
                user.username.as("username")
        ))
                .from(user)
                .where(user.id.in(userIds))
                .fetch();
    }

    @Override
    public void updateUserInfo(Long id, String username, String password, String city, String town, String address) {
        jpaQueryFactory.update(user)
                .set(user.username, username)
                .set(user.password, password)
                .set(user.city, city)
                .set(user.town, town)
                .set(user.address, address)
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
