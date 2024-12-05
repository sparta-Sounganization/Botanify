package com.sounganization.botanify.domain.user.entity;

import com.sounganization.botanify.domain.user.enums.UserRole;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private UserRole role;

    // 주소 관련
    @Column(nullable = false, length = 50)
    private String city;

    @Column(nullable = false, length = 50)
    private String town;

    @Column(nullable = false)
    private String address;

}
