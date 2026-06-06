package com.Man10h.book_store.services.integration_test;

import com.Man10h.book_store.model.dto.UserDTO;
import com.Man10h.book_store.model.dto.UserLoginDTO;
import com.Man10h.book_store.model.entity.RoleEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.repository.RoleRepository;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.MailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.ArrayList;
import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class HomeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MailService mailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql =
            new MySQLContainer<>("mysql:8.0")
                    .withDatabaseName("book-store")
                    .withUsername("root")
                    .withPassword("root");



    @Test
    public void should_register_user() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .email("test@gmail.com")
                .password("password")
                .username("test")
                .build();

        mockMvc.perform(post("/api/v1/home/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isOk());

        UserEntity user = userRepository.findByEmail("test@gmail.com").orElseThrow();

        assertEquals("test", user.getUsername());
        verify(mailService, times(1)).sendMail(any(), any(), any());
    }

    @Test
    public void should_verify_user() throws Exception {
        // Arrange
        UserEntity user = UserEntity.builder()
                .email("verify@gmail.com")
                .password("password")
                .username("verify")
                .verificationCode("1234678")
                .verificationCodeExpiration(new Date(new Date().getTime() + 1000 * 15 * 60))
                .enabled(false) // Nên set rõ trạng thái false ban đầu
                .build();
        userRepository.save(user);

        // Act
        mockMvc.perform(get("/api/v1/home/verify")
                        .param("email", user.getEmail())
                        .param("code", user.getVerificationCode()))
                .andExpect(status().isOk());

        // Assert
        UserEntity updatedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
        // [SỬA LỖI 2]: Assert trên đối tượng updatedUser thay vì user cũ
        assertTrue(updatedUser.getEnabled(), "User should be enabled after verification");
    }

    @Test
    public void should_not_register_user_when_username_exists() throws Exception {
        UserEntity user = UserEntity.builder()
                .username("test")
                .build();
        userRepository.save(user);

        UserDTO userDTO = UserDTO.builder()
                .username("test")
                .password("password")
                .email("test@gmail.com")
                .build();

        mockMvc.perform(
                post("/api/v1/home/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
        )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Account already exists")
                );

        assertEquals("test", user.getUsername());
        verify(mailService, never()).sendMail(any(), any(), any());
    }

    @Test
    public void should_verify_failed() throws Exception{
        String email = "test@gmail.com";
        String code = "1234678";
        String wrongCode = "12131";
        UserEntity user = UserEntity.builder()
                .username("test")
                .email(email)
                .verificationCode(code)
                .verificationCodeExpiration(new Date(new Date().getTime() + 1000 * 15 * 60))
                .build();

        userRepository.save(user);

        mockMvc.perform(get("/api/v1/home/verify")
                .param("email", email)
                .param("code", wrongCode))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_not_verify() throws Exception {
        String email = "test@gmail.com";
        String code = "1234678";

        mockMvc.perform(get("/api/v1/home/verify")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));
    }

    @Test
    public void should_verify_failed_when_code_expired() throws Exception{
        String email = "test@gmail.com";
        String code = "1234678";
        UserEntity user = UserEntity.builder()
                .username("test")
                .email(email)
                .verificationCode(code)
                .verificationCodeExpiration(new Date(new Date().getTime() - 1000 * 15 * 60))
                .build();

        userRepository.save(user);

        mockMvc.perform(get("/api/v1/home/verify")
                        .param("email", email)
                        .param("code", code))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void should_login() throws Exception{
        //Arrange
        String username = "test";
        String password = "test";
        RoleEntity roleEntity = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleEntity);

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roleEntity(roleEntity)
                .enabled(true)
                .build();
        userRepository.save(user);

        UserLoginDTO userDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();

        //Act + Assert
        mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
        ).andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void should_login_failed_when_wrong_password() throws Exception{
        //Arrange
        String username = "test";
        String password = "test";
        String wrongPassword = "wrongPassword";
        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .build();
        userRepository.save(user);

        UserLoginDTO userDTO = UserLoginDTO.builder()
                .username(username)
                .password(wrongPassword)
                .build();

        //Act + Assert
        mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void should_login_failed_when_not_found_user() throws Exception {
        String username = "test";
        String password = "test";

        UserLoginDTO userDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();

        mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
        ).andExpect(status().isNotFound());
    }

    @Test
    public void should_login_failed_when_user_disabled() throws Exception {
        //Arrange
        String username = "test";
        String password = "test";
        RoleEntity roleEntity = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleEntity);

        UserEntity user = UserEntity.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roleEntity(roleEntity)
                .enabled(false)
                .build();
        userRepository.save(user);

        UserLoginDTO userDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();

        //Act + Assert
        mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO))
        ).andExpect(status().isUnauthorized());
    }

    @Test
    public void should_resend_email() throws Exception {
        //Arrange
        String username = "test";
        String password = "test";
        String email = "test@gmail.com";
        UserEntity user = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .enabled(false)
                .build();
        userRepository.save(user);

        //Act + Assert
        mockMvc.perform(get("/api/v1/home/resend")
                .param("email", email))
                .andExpect(status().isOk());

        verify(mailService, times(1)).sendMail(any(), any(), any());
    }

    @Test
    public void should_resend_failed_when_account_not_found() throws Exception {
        //Arrange
        String email = "test@gmail.com";

        //Act + Assert
        mockMvc.perform(get("/api/v1/home/resend")
                        .param("email", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));

        verify(mailService, never()).sendMail(any(), any(), any());
    }

    @Test
    public void should_not_resend_when_account_enabled() throws Exception {
        //Arrange
        String username = "test";
        String password = "test";
        String email = "test@gmail.com";
        UserEntity user = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .enabled(true)
                .build();
        userRepository.save(user);

        //Act + Assert
        mockMvc.perform(get("/api/v1/home/resend")
                        .param("email", email))
                .andExpect(status().isBadRequest());

        verify(mailService, never()).sendMail(any(), any(), any());
    }


    @Test
    public void should_forget_password() throws Exception {
        String username = "test";
        String password = "test";
        String email = "test@gmail.com";
        UserEntity user = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .enabled(true)
                .build();
        userRepository.save(user);

        //Act + Assert
        mockMvc.perform(get("/api/v1/home/forgot-password")
                        .param("email", email))
                .andExpect(status().isOk());

        verify(mailService, times(1)).sendMail(any(), any(), any());
    }

    @Test
    public void should_not_forget_password_when_account_disabled() throws Exception {
        String username = "test";
        String password = "test";
        String email = "test@gmail.com";
        UserEntity user = UserEntity.builder()
                .email(email)
                .username(username)
                .password(password)
                .enabled(false)
                .build();
        userRepository.save(user);

        //Act + Assert
        mockMvc.perform(get("/api/v1/home/forgot-password")
                        .param("email", email))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Account disabled"));

        verify(mailService, never()).sendMail(any(), any(), any());
    }

    @Test
    public void should_not_forget_password_when_not_found_account() throws Exception {
        String email = "test@gmail.com";

        mockMvc.perform(get("/api/v1/home/forgot-password")
                        .param("email", email))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Account not found"));

        verify(mailService, never()).sendMail(any(), any(), any());
    }
}