package com.Man10h.book_store.services.unit_test;

import com.Man10h.book_store.exception.business.AccountDisabledException;
import com.Man10h.book_store.exception.business.AccountExistsException;
import com.Man10h.book_store.exception.business.AccountNotFoundException;
import com.Man10h.book_store.exception.business.RoleNotFoundException;
import com.Man10h.book_store.exception.client.AuthenticationFailException;
import com.Man10h.book_store.model.dto.UserDTO;
import com.Man10h.book_store.model.dto.UserLoginDTO;
import com.Man10h.book_store.model.entity.CartEntity;
import com.Man10h.book_store.model.entity.RoleEntity;
import com.Man10h.book_store.model.entity.UserEntity;
import com.Man10h.book_store.repository.CartRepository;
import com.Man10h.book_store.repository.RoleRepository;
import com.Man10h.book_store.repository.UserRepository;
import com.Man10h.book_store.service.MailService;
import com.Man10h.book_store.service.TokenService;
import com.Man10h.book_store.service.impl.AuthenticationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private MailService mailService;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private CartRepository cartRepository;


    @Spy
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
//any() -> tham so mock() -> object
    @Test
    public void should_login(){
        UserLoginDTO userDTO = UserLoginDTO.builder()
                .username("test")
                .password("test")
                .build();
        UsernamePasswordAuthenticationToken
                authenticationToken = new UsernamePasswordAuthenticationToken(userDTO.getUsername(), userDTO.getPassword());
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);
        UserEntity userEntity = mock(UserEntity.class);
        when(authentication.getPrincipal()).thenReturn(userEntity);
        when(userEntity.getEnabled()).thenReturn(true);
        String token = "token";
        when(tokenService.generateToken(userEntity)).thenReturn(token);

        assertEquals(token, authenticationService.login(userDTO));
        verify(authenticationManager).authenticate(authenticationToken);
        verify(tokenService).generateToken(userEntity);
        verify(authentication).getPrincipal();
        verify(userEntity).getEnabled();
    }

    @Test
    public void should_throw_authentication_fail(){
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username("test")
                .password("test")
                .build();
        UsernamePasswordAuthenticationToken
                authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());

        when(authenticationManager.authenticate(authenticationToken))
                .thenThrow(AuthenticationFailException.class);

        assertThrows(AuthenticationFailException.class, () -> authenticationService.login(userLoginDTO));
        verify(authenticationManager).authenticate(authenticationToken);
    }

    @Test
    public void should_throw_account_disabled(){
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .username("test")
                .password("test")
                .build();
        UsernamePasswordAuthenticationToken
                authenticationToken = new UsernamePasswordAuthenticationToken(userLoginDTO.getUsername(), userLoginDTO.getPassword());

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(authenticationToken)).thenReturn(authentication);

        UserEntity userEntity = mock(UserEntity.class);
        when(authentication.getPrincipal()).thenReturn(userEntity);
        when(userEntity.getEnabled()).thenReturn(false);

        assertThrows(AccountDisabledException.class, () -> authenticationService.login(userLoginDTO));
        verify(authenticationManager).authenticate(authenticationToken);
        verify(authentication).getPrincipal();
        verify(userEntity).getEnabled();
    }

    @Test
    public void should_register(){
        UserDTO userDTO = UserDTO.builder()
                .username("test")
                .password("test")
                .email("test@test.com")
                .build();

        when(userRepository.findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("password_encoded");
        doReturn("12345")
                .when(authenticationService).generateCode();
        RoleEntity roleEntity = new RoleEntity(1L, "USER", new ArrayList<>());
        when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
        UserEntity userEntity = UserEntity.builder()
                .username(userDTO.getUsername())
                .roleEntity(roleEntity)
                .password("password_encoded")
                .email(userDTO.getEmail())
                .verificationCode(authenticationService.generateCode())
                .verificationCodeExpiration(new Date(new Date().getTime() + 15 * 60 * 1000))
                .enabled(false)
                .build();

        when(userRepository.save(any())).thenReturn(userEntity);

        CartEntity cartEntity = CartEntity.builder()
                .itemEntityList(new ArrayList<>())
                .userEntity(userEntity)
                .build();
        when(cartRepository.save(any())).thenReturn(cartEntity);

        doNothing().when(authenticationService).send(any(), any(), any());

        assertTrue(authenticationService.register(userDTO));
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(cartRepository, times(1)).save(any(CartEntity.class));
        verify(userRepository, times(1)).findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail());
        verify(passwordEncoder, times(1)).encode(any());
        verify(authenticationService, times(1)).send(any(), any(), any());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void should_throw_account_exists_exception_when_register(){
        UserDTO userDTO = UserDTO.builder()
                .username("test")
                .password("test")
                .email("test@test.com")
                .build();
        when(userRepository.findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail()))
                .thenReturn(Optional.of(mock(UserEntity.class)));

        assertThrows(AccountExistsException.class, () -> authenticationService.register(userDTO));
        verify(userRepository, times(1)).findByUsernameOrEmail(any(), any());
    }

    @Test
    public void should_throw_role_not_found_exception_when_register(){
        UserDTO userDTO = UserDTO.builder()
                .username("test")
                .password("test")
                .email("test@test.com")
                .build();

        when(userRepository.findByUsernameOrEmail(userDTO.getUsername(), userDTO.getEmail())).thenReturn(Optional.empty());
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> authenticationService.register(userDTO));
        verify(userRepository, times(1)).findByUsernameOrEmail(any(), any());
        verify(roleRepository, times(1)).findById(1L);
    }

    @Test
    public void should_verify() {
        String email = "test@test.com";
        String code = "12345";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .verificationCode(code)
                .verificationCodeExpiration(new Date(new Date().getTime() + 15 * 60 * 1000))
                .enabled(false)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        assertTrue(authenticationService.verify(email, code));
        verify(userRepository, times(1)).findByEmail(email);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    public void should_throw_account_not_found_exception_when_verify(){
        String email = "test@test.com";
        String code = "12345";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> authenticationService.verify(email, code));
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    public void should_not_verify_when_wrong_code(){
        String email = "test@test.com";
        String code = "12345";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .verificationCode("21901")
                .verificationCodeExpiration(new Date(new Date().getTime() + 15 * 60 * 1000))
                .enabled(false)
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userEntity));
        assertFalse(authenticationService.verify(email, code));
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    public void should_not_verify_when_expired(){
        String email = "test@test.com";
        String code = "12345";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .verificationCode("21901")
                .verificationCodeExpiration(new Date(new Date().getTime() - 15 * 60 * 1000))
                .enabled(false)
                .build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userEntity));
        assertFalse(authenticationService.verify(email, code));
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    public void should_resend(){
        String email = "test@test.com";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .enabled(false)
                .build();
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        doNothing().when(authenticationService).send(any(), any(), any());

        assertTrue(authenticationService.resendVerificationCode(email));
        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(authenticationService, times(1)).send(any(), any(), any());
    }

    @Test
    public void should_throw_account_not_found_exception_when_resend(){
        String email = "test@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> authenticationService.resendVerificationCode(email));
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    public void should_false_when_resend(){
        String email = "test@test.com";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(any())).thenReturn(Optional.of(userEntity));
        assertFalse(authenticationService.resendVerificationCode(email));
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    public void should_forgot_password(){
        String email = "test@test.com";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .enabled(true)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);
        when(passwordEncoder.encode(anyString())).thenReturn("password");
        doNothing().when(authenticationService).send(any(), any(), any());

        assertTrue(authenticationService.forgotPassword(email));
        assertEquals("password", userEntity.getPassword());
        verify(userRepository, times(1)).findByEmail(any());
        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(authenticationService, times(1)).send(any(), any(), any());
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    public void should_throw_account_not_found_exception_when_forgot_password(){
        String email = "test@test.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(AccountNotFoundException.class, () -> authenticationService.forgotPassword(email));
        verify(userRepository, times(1)).findByEmail(any());
    }

    @Test
    public void should_throw_account_disabled_exception_when_forgot_password(){
        String email = "test@test.com";

        UserEntity userEntity = UserEntity.builder()
                .email(email)
                .enabled(false)
                .build();
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));

        assertThrows(AccountDisabledException.class, () -> authenticationService.forgotPassword(email));
        verify(userRepository, times(1)).findByEmail(any());
    }
}
