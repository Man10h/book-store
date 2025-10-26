package com.Man10h.book_store.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private Boolean enabled;
    private String verificationCode;
    private LocalTime verificationCodeExpiration;

    @OneToMany(mappedBy = "userEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<CartEntity> cartEntityList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity;
}

