package com.Man10h.book_store.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity implements Serializable, UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + roleEntity.getName());
        return Collections.singleton(authority);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String email;
    private Boolean enabled;
    private String verificationCode;
    private Date verificationCodeExpiration;

    @OneToMany(mappedBy = "userEntity", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<CartEntity> cartEntityList = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "role_id")
    private RoleEntity roleEntity;
}

