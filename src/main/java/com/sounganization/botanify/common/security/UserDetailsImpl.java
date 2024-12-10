package com.sounganization.botanify.common.security;

import com.sounganization.botanify.domain.user.dto.req.UserReqDto;
import com.sounganization.botanify.domain.user.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
@RequiredArgsConstructor
public class UserDetailsImpl implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String city;
    private final String town;
    private final UserRole role;

    public UserDetailsImpl(UserReqDto userReqDto) {
        this.id = userReqDto.id();
        this.username = userReqDto.username();
        this.password = userReqDto.password();
        this.city = userReqDto.city();
        this.town = userReqDto.town();
        this.role = userReqDto.role();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role::name);
    }
}
