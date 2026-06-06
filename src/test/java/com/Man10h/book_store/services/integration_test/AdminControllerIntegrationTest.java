package com.Man10h.book_store.services.integration_test;

import com.Man10h.book_store.model.dto.BookDTO;
import com.Man10h.book_store.model.dto.UserLoginDTO;
import com.Man10h.book_store.model.entity.BookEntity;
import com.Man10h.book_store.model.entity.RoleEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.model.response.UserResponse;
import com.Man10h.book_store.repository.BookRepository;
import com.Man10h.book_store.repository.RoleRepository;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.CloudinaryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureMockMvc
@Transactional
public class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private CloudinaryService cloudinaryService;

    @Autowired
    private EntityManager em;

    @Container
    @ServiceConnection
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("book-store")
            .withUsername("root")
            .withPassword("root");
    @Autowired
    private BookRepository bookRepository;


    @Test
    public void should_update_user_role() throws Exception {
        RoleEntity roleUser = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleUser);

        RoleEntity roleAdmin = RoleEntity.builder()
                .id(2L)
                .name("ADMIN")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleAdmin);

        UserEntity userAdmin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .enabled(true)
                .roleEntity(roleAdmin)
                .build();
        userRepository.save(userAdmin);


        UserEntity user = UserEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .enabled(true)
                .roleEntity(roleUser)
                .build();
        Long userId = userRepository.save(user).getId();

        UserLoginDTO adminLoginDTO = UserLoginDTO.builder()
                .username("admin")
                .password("admin")
                .build();
        //login
        //act + assert
        MvcResult resultLogin = mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminLoginDTO))).andExpect(status().isOk()).andReturn();
        String token = resultLogin.getResponse().getContentAsString();
        System.out.println(token);
        //get all users
        //act
        MvcResult resultGetAllUsers = mockMvc.perform(
                get("/api/v1/admin/users")
                        .param("size", "10")
                        .param("page", "0")
                .header("Authorization", "Bearer " + token)
        ).andExpect(status().isOk()).andReturn();


        //assert
        JsonNode root = om.readTree(resultGetAllUsers.getResponse().getContentAsString());

        List<UserResponse> userResponseList =
                om.readValue(root.get("content").toString(), new TypeReference<List<UserResponse>>(){});
        assertEquals(2, userResponseList.size());


        //update user by id
        //act + assert
        mockMvc.perform(put("/api/v1/admin/users/{userId}", userId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        em.flush();
        em.clear();

        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        assertTrue(optionalUser.isPresent());
        assertEquals("ADMIN", optionalUser.get().getRoleEntity().getName());
    }


    @Test
    public void should_not_update_user_role_when_user_not_found() throws Exception {
        RoleEntity roleUser = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleUser);

        RoleEntity roleAdmin = RoleEntity.builder()
                .id(2L)
                .name("ADMIN")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleAdmin);

        UserEntity userAdmin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .enabled(true)
                .roleEntity(roleAdmin)
                .build();
        userRepository.save(userAdmin);


        Long userId = 10L;

        UserLoginDTO adminLoginDTO = UserLoginDTO.builder()
                .username("admin")
                .password("admin")
                .build();
        //login
        //act + assert
        MvcResult resultLogin = mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminLoginDTO))).andExpect(status().isOk()).andReturn();
        String token = resultLogin.getResponse().getContentAsString();
        System.out.println(token);

        //update user by id
        //act + assert
        mockMvc.perform(put("/api/v1/admin/users/{userId}", userId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

    }

    @Test
    public void should_not_update_user_role_when_user_is_not_enabled() throws Exception {
        RoleEntity roleUser = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleUser);

        RoleEntity roleAdmin = RoleEntity.builder()
                .id(2L)
                .name("ADMIN")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleAdmin);

        UserEntity userAdmin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .enabled(true)
                .roleEntity(roleAdmin)
                .build();
        userRepository.save(userAdmin);


        UserEntity user = UserEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .enabled(false)
                .roleEntity(roleUser)
                .build();
        Long userId = userRepository.save(user).getId();

        UserLoginDTO adminLoginDTO = UserLoginDTO.builder()
                .username("admin")
                .password("admin")
                .build();
        //login
        //act + assert
        MvcResult resultLogin = mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminLoginDTO))).andExpect(status().isOk()).andReturn();
        String token = resultLogin.getResponse().getContentAsString();
        System.out.println(token);


        //update user by id
        //act + assert
        mockMvc.perform(put("/api/v1/admin/users/{userId}", userId).header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }


    @Test
    public void should_not_delete_user_role_when_user_not_found() throws Exception {
        RoleEntity roleUser = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleUser);

        RoleEntity roleAdmin = RoleEntity.builder()
                .id(2L)
                .name("ADMIN")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleAdmin);

        UserEntity userAdmin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .enabled(true)
                .roleEntity(roleAdmin)
                .build();
        userRepository.save(userAdmin);

        Long userId = 10L;

        UserLoginDTO adminLoginDTO = UserLoginDTO.builder()
                .username("admin")
                .password("admin")
                .build();
        //login
        //act + assert
        MvcResult resultLogin = mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminLoginDTO))).andExpect(status().isOk()).andReturn();
        String token = resultLogin.getResponse().getContentAsString();
        System.out.println(token);

        //update user by id
        //act + assert
        mockMvc.perform(delete("/api/v1/admin/users/{userId}", userId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

    }

    @Test
    public void should_delete_user() throws Exception {
        RoleEntity roleUser = RoleEntity.builder()
                .id(1L)
                .name("USER")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleUser);

        RoleEntity roleAdmin = RoleEntity.builder()
                .id(2L)
                .name("ADMIN")
                .userEntityList(new ArrayList<>())
                .build();
        roleRepository.save(roleAdmin);

        UserEntity userAdmin = UserEntity.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin"))
                .enabled(true)
                .roleEntity(roleAdmin)
                .build();
        userRepository.save(userAdmin);


        UserEntity user = UserEntity.builder()
                .username("user")
                .password(passwordEncoder.encode("user"))
                .roleEntity(roleUser)
                .build();
        Long userId = userRepository.save(user).getId();

        UserLoginDTO adminLoginDTO = UserLoginDTO.builder()
                .username("admin")
                .password("admin")
                .build();
        //login
        //act + assert
        MvcResult resultLogin = mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(adminLoginDTO))).andExpect(status().isOk()).andReturn();
        String token = resultLogin.getResponse().getContentAsString();
        System.out.println(token);


        //update user by id
        //act + assert
        mockMvc.perform(delete("/api/v1/admin/users/{userId}", userId).header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        em.flush();
        em.clear();
        Optional<UserEntity> optionalUser = userRepository.findById(userId);
        assertFalse(optionalUser.isPresent());
    }




}
