package com.Man10h.book_store.services.integration_test;


import com.Man10h.book_store.model.dto.ItemDTO;
import com.Man10h.book_store.model.dto.UserLoginDTO;
import com.Man10h.book_store.model.entity.*;
import com.Man10h.book_store.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EntityManager entityManager;

    @Container
    @ServiceConnection
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("book-store")
            .withUsername("root")
            .withPassword("root");

    @Test
    public void should_manage_cart() throws Exception {
        //Arrange
        BookEntity bookEntity = BookEntity.builder()
                .imageEntityList(new ArrayList<>())
                .imageEntityList(new ArrayList<>())
                .orderItemEntityList(new ArrayList<>())
                .title("book title")
                .author("author")
                .type("type")
                .price(100.1)
                .description("description")
                .build();
        Long bookId = bookRepository.save(bookEntity).getId();

        RoleEntity role = RoleEntity.builder()
                .userEntityList(new ArrayList<>())
                .name("USER")
                .id(1L)
                .build();
        roleRepository.save(role);

        String username = "test";
        String password = "test";
        UserEntity user = UserEntity.builder()
                .username(username)
                .roleEntity(role)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .cartEntityList(new ArrayList<>())
                .build();
        userRepository.save(user);

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();

        //login
        //Act + Assert
        MvcResult result = mockMvc.perform(post("/api/v1/home/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userLoginDTO))
        ).andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getContentAsString();


        //get cart
        //Act
        mockMvc.perform(get("/api/v1/user/carts")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        //Assert
        List<CartEntity> carts = cartRepository.findByUserEntity(user);
        assertFalse(carts.isEmpty());
        Long cartId = carts.getFirst().getId();
        //add to cart
        //Act
        ItemDTO itemDTO = ItemDTO.builder()
                .quantity(1L)
                .status("status")
                .build();
        mockMvc.perform(post("/api/v1/user/carts/items/{bookId}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(itemDTO))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        //Assert
        List<ItemEntity> itemEntityList = itemRepository.findByCartEntity_Id(cartId);
        assertFalse(itemEntityList.isEmpty());
        assertEquals(1, itemEntityList.getFirst().getQuantity());
        Long itemId = itemEntityList.getFirst().getId();

        //update item
        //Act
        ItemDTO updateItem = ItemDTO.builder()
                .quantity(2L)
                .status("status")
                .build();

        mockMvc.perform(put("/api/v1/user/items/{itemId}", itemId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateItem))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        //Assert
        ItemEntity itemEntity = itemRepository.findById(itemId).get();
        assertEquals(2L, itemEntity.getQuantity());

        //delete item
        //Act
        mockMvc.perform(delete("/api/v1/user/items/{itemId}", itemId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();
        //Assert
        Optional<ItemEntity> optionalItemEntity = itemRepository.findById(itemId);
        assertTrue(optionalItemEntity.isEmpty());
    }

    @Test
    public void should_not_add_to_cart_when_book_not_found() throws Exception {
        RoleEntity role = RoleEntity.builder()
                .userEntityList(new ArrayList<>())
                .name("USER")
                .id(1L)
                .build();
        roleRepository.save(role);

        String username = "test";
        String password = "test";
        UserEntity user = UserEntity.builder()
                .username(username)
                .roleEntity(role)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .cartEntityList(new ArrayList<>())
                .build();
        userRepository.save(user);

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();

        //login
        //Act + Assert
        MvcResult result = mockMvc.perform(post("/api/v1/home/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDTO))
                ).andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getContentAsString();

        //get cart
        //Act
        mockMvc.perform(get("/api/v1/user/carts")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        //Assert
        List<CartEntity> carts = cartRepository.findByUserEntity(user);
        assertFalse(carts.isEmpty());
        Long cartId = carts.getFirst().getId();


        //add to cart
        //Act
        Long bookId = 1L;
        ItemDTO itemDTO = ItemDTO.builder()
                .quantity(1L)
                .status("status")
                .build();
        mockMvc.perform(post("/api/v1/user/carts/items/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDTO))
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());

        //Assert
        List<ItemEntity> itemEntityList = itemRepository.findByCartEntity_Id(cartId);
        assertTrue(itemEntityList.isEmpty());

    }

    @Test
    public void should_not_update_item_when_item_not_found() throws Exception {
        RoleEntity role = RoleEntity.builder()
                .userEntityList(new ArrayList<>())
                .name("USER")
                .id(1L)
                .build();
        roleRepository.save(role);

        String username = "test";
        String password = "test";
        UserEntity user = UserEntity.builder()
                .username(username)
                .roleEntity(role)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .cartEntityList(new ArrayList<>())
                .build();
        userRepository.save(user);

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();

        //login
        //Act + Assert
        MvcResult result = mockMvc.perform(post("/api/v1/home/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDTO))
                ).andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getContentAsString();

        //update item
        ItemDTO updateItem = ItemDTO.builder()
                .quantity(2L)
                .status("status")
                .build();
        Long itemId = 1L;

        //Act
        mockMvc.perform(put("/api/v1/user/items/{itemId}", itemId)
                        .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateItem))
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_not_delete_item_when_item_not_found() throws Exception {
        RoleEntity role = RoleEntity.builder()
                .userEntityList(new ArrayList<>())
                .name("USER")
                .id(1L)
                .build();
        roleRepository.save(role);

        String username = "test";
        String password = "test";
        UserEntity user = UserEntity.builder()
                .username(username)
                .roleEntity(role)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .cartEntityList(new ArrayList<>())
                .build();
        userRepository.save(user);

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();

        //login
        //Act + Assert
        MvcResult result = mockMvc.perform(post("/api/v1/home/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDTO))
                ).andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getContentAsString();

        //delete item
        Long itemId = 1L;

        //Act
        mockMvc.perform(delete("/api/v1/user/items/{itemId}", itemId).header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void should_not_delete_items_not_yours() throws Exception {
        RoleEntity role = RoleEntity.builder()
                .userEntityList(new ArrayList<>())
                .name("USER")
                .id(1L)
                .build();
        roleRepository.save(role);

        String username = "test";
        String password = "test";
        UserEntity user = UserEntity.builder()
                .username(username)
                .roleEntity(role)
                .password(passwordEncoder.encode(password))
                .enabled(true)
                .cartEntityList(new ArrayList<>())
                .build();
        userRepository.save(user);

        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username(username)
                .password(password)
                .build();
        ItemEntity itemEntity = ItemEntity.builder()
                .quantity(1L)
                .status("status")
                .build();
        Long itemId = itemRepository.save(itemEntity).getId();
        //login
        //Act + Assert
        MvcResult result = mockMvc.perform(post("/api/v1/home/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginDTO))
                ).andExpect(status().isOk())
                .andReturn();
        String token = result.getResponse().getContentAsString();

        //Act
        mockMvc.perform(delete("/api/v1/user/items/{itemId}", itemId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}
