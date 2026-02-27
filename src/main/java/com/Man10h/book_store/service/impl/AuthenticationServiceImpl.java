package com.Man10h.book_store.service.impl;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.model.dto.UserDTO;
import com.Man10h.book_store.model.dto.UserLoginDTO;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.RoleEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.response.UserResponse;
import com.Man10h.book_store.repository.CartRepository;
import com.Man10h.book_store.repository.RoleRepository;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.AuthenticationService;
import com.Man10h.book_store.service.MailService;
import com.Man10h.book_store.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final MailService mailService;
    private final RoleRepository roleRepository;
    private final CartRepository cartRepository;
    private final RestTemplate restTemplate;
    private final OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Override
    public String login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return null;
        }
        UserEntity user = optionalUser.get();
        if(!passwordEncoder.matches(password, user.getPassword()) || !user.getEnabled()) {
            return null;
        }
       UsernamePasswordAuthenticationToken authenticationToken = new
               UsernamePasswordAuthenticationToken(username, password, user.getAuthorities());
       authenticationManager.authenticate(authenticationToken);

        return tokenService.generateToken(user);
    }

    @Transactional
    public boolean register(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();
        String email = userDTO.getEmail();
        Optional<UserEntity> optionalUsername = userRepository.findByUsername(username);
        if (optionalUsername.isPresent()) {
            return false;
        }
        Optional<UserEntity> optionalEmail = userRepository.findByEmail(email);
        if (optionalEmail.isPresent()) {
            return false;
        }
        RoleEntity role = roleRepository.findById(1L).orElse(null);
        String code = generateCode();
        UserEntity user = UserEntity.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .cartEntityList(new ArrayList<>())
                .roleEntity(role)
                .enabled(false)
                .verificationCode(code)
                .verificationCodeExpiration(new Date(new Date().getTime() + 1000 * 15 * 60))
                .build();


        userRepository.save(user);

        CartEntity cartEntity = CartEntity.builder()
                .itemEntityList(new ArrayList<>())
                .userEntity(user)
                .build();
        cartRepository.save(cartEntity);


        send(email, "Verification Code: ", code);

        return true;
    }

    @Override
    public boolean verify(String email, String code) {
        Optional<UserEntity> optionalEmail = userRepository.findByEmail(email);
        if(optionalEmail.isEmpty()){
            return false;
        }
        UserEntity user = optionalEmail.get();

        if(!code.equals(user.getVerificationCode())
                || new Date().after(user.getVerificationCodeExpiration())){
            return false;
        }
        user.setVerificationCode(null);
        user.setVerificationCodeExpiration(null);
        user.setEnabled(true);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean resendVerificationCode(String email) {
        Optional<UserEntity> optionalEmail = userRepository.findByEmail(email);
        if(optionalEmail.isEmpty()){
            return false;
        }
        UserEntity user = optionalEmail.get();

        if(user.getEnabled()){
            return false;
        }
        String code = generateCode();
        user.setVerificationCode(code);
        user.setVerificationCodeExpiration(new Date(new Date().getTime() + 1000 * 15 * 60));
        userRepository.save(user);

        send(email, "Verification Code: ", code);
        return true;
    }

    @Override
    public boolean forgotPassword(String email) {
        Optional<UserEntity> optionalEmail = userRepository.findByEmail(email);
        if(optionalEmail.isEmpty()){
            return false;
        }
        UserEntity user = optionalEmail.get();
        if(!user.getEnabled()){
            return false;
        }
        String password = generateCode();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        send(email, "New Password: ", password);
        return true;
    }

    @Override
    @Cacheable(value = "tokens", key = "#token")
    public UserResponse getUserByToken(String token) {
        String username = tokenService.getUsername(token);
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);
        if(optionalUser.isEmpty()){
            return null;
        }
        UserEntity user = optionalUser.get();
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(user.getRoleEntity().getName())
                .build();
    }

    @Override
    @Cacheable(value = "tokens", key = "#accessToken")
    public String oauth2Token(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://openidconnect.googleapis.com/v1/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
        );

        if(response.getStatusCode().isError()){
            return null;
        }

        String email = response.getBody().get("email").toString();
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new ErrorException("Invalid email");
        }

        UserEntity user = optionalUser.get();
        return tokenService.generateToken(user);
    }

    @Override
    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null){
            throw new ErrorException("Not logged in");
        }
        SecurityContextHolder.clearContext();
    }

    public String generateCode(){
        return new Random().nextInt(100000) + "";
    }

    public void send(String to, String subject, String content) {
        String html = "<html>" + subject + ": " + content + "</html>";
        mailService.sendMail(to, subject, html);
    }
}
