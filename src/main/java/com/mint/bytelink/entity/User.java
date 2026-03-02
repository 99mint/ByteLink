package com.mint.bytelink.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private RefreshToken refreshToken;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UrlDetails> urls = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false , unique = true)
    private String email;

    private String username;

    private boolean isAccountNonExpired = true;

    private boolean isCredentialsNonExpired = true;

    private boolean isAccountNonLocked = true;

    private boolean isEnable = true;


    public enum Role{
        USER,
        ADMIN
    }
}
