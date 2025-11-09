package com.Man10h.book_store.service;

import com.Man10h.book_store.model.dto.UserDTO;
import com.Man10h.book_store.model.dto.UserLoginDTO;
import com.Man10h.book_store.model.response.UserResponse;

public interface AuthenticationService {
    public String login(UserLoginDTO userLoginDTO);
    public boolean register(UserDTO userDTO);
    public boolean verify(String email, String code);
    public boolean resendVerificationCode(String email);
    public boolean forgotPassword(String email);
    public UserResponse getUserByToken(String token);
    public String oauth2Token(String accessToken);
    public void logout();
}
