package com.Man10h.book_store.util;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.entity.RoleEntity;
import com.Man10h.book_store.repository.CartRepository;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.repository.RoleRepository;
import com.Man10h.book_store.service.TokenService;
import com.Man10h.book_store.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler implements AuthenticationSuccessHandler {
    private final TokenService tokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");

        Optional<UserEntity> optional = userRepository.findByEmail(email);
        
        UserEntity userEntity;
        if (optional.isEmpty()) {
            // Create new user if doesn't exist
            String username = generateUniqueUsername(email);
            RoleEntity userRole = roleRepository.findById(1L)
                    .orElseThrow(() -> new ErrorException("User role not found"));
            
            userEntity = UserEntity.builder()
                    .username(username)
                    .email(email)
                    .password("") // OAuth2 users don't need password
                    .enabled(true)
                    .cartEntityList(new ArrayList<>())
                    .roleEntity(userRole)
                    .build();

            userRepository.save(userEntity);

            CartEntity cartEntity = CartEntity.builder()
                    .itemEntityList(new ArrayList<>())
                    .userEntity(userEntity)
                    .build();
            cartRepository.save(cartEntity);

        } else {
            userEntity = optional.get();
        }

        String jwt = tokenService.generateToken(userEntity);

        response.sendRedirect(
                frontendUrl + "/oauth2/callback?token=" + jwt
        );
    }

    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;
        
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
}
