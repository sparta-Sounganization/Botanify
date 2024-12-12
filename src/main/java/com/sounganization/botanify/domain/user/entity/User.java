package com.sounganization.botanify.domain.user.entity;

import com.sounganization.botanify.common.entity.Timestamped;
import com.sounganization.botanify.common.security.UserDetailsImpl;
import com.sounganization.botanify.domain.user.enums.UserRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Builder 사용을 위해 추가, 무분별한 접근을 막기 위해 추가
public class User extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    // 주소 관련
    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String town;

    @Column(nullable = false)
    private String address;

    @Builder
    public User(String email, String username, String password, UserRole role, String city, String town, String address) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.role = role;
        this.city = city;
        this.town = town;
        this.address = address;
    }

    public UserDetailsImpl toUserDetails() {
        return new UserDetailsImpl(id, username, email, password, city, town, role);
    }
}
