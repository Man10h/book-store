package com.Man10h.book_store.controller;

import com.Man10h.book_store.exception.exception.ErrorException;
import com.Man10h.book_store.model.dto.ChatMessage;
import com.Man10h.book_store.model.dto.UserDTO;
import com.Man10h.book_store.model.dto.UserLoginDTO;
import com.Man10h.book_store.model.response.UserResponse;
import com.Man10h.book_store.service.AuthenticationService;
import com.Man10h.book_store.service.BookService;
import com.Man10h.book_store.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/home")
@RequiredArgsConstructor
public class HomeController {

    private final AuthenticationService authenticationService;
    private final BookService bookService;
    private final UserService userService;


    @GetMapping("/books")
    public ResponseEntity<?> getBooks(@RequestParam(name = "text") String text,
                                      @RequestParam(name = "type") String type,
                                      @RequestParam(name = "page") int page,
                                      @RequestParam(name = "size") int size){
        return ResponseEntity.ok(bookService.findByTitleAndAuthorAndType(text, type, PageRequest.of(page, size)));
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<?> getBook(@PathVariable(name = "id") Long id){
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginDTO userLoginDTO,
                                   BindingResult bindingResult) {
        try{
            if(bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().build();
            }
            String token = authenticationService.login(userLoginDTO);
            if(token == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO,
                                      BindingResult bindingResult) {
        try{
            if(bindingResult.hasErrors()) {
                return ResponseEntity.badRequest().build();
            }
            boolean result = authenticationService.register(userDTO);
            if(!result) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam(name = "email") String email,
                                    @RequestParam(name = "code") String code) {
        try{
            boolean result = authenticationService.verify(email, code);
            if(!result) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @GetMapping("/resend")
    public ResponseEntity<?> verify(@RequestParam(name = "email") String email) {
        try{
            boolean result = authenticationService.resendVerificationCode(email);
            if(!result) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @GetMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam(name = "email") String email) {
        try{
            boolean result = authenticationService.forgotPassword(email);
            if(!result) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @GetMapping("/token-info")
    public ResponseEntity<?> tokenInfo(@RequestParam(name = "token") String token) {
        try{
            UserResponse result = authenticationService.getUserByToken(token);
            if(result == null) {
                return ResponseEntity.badRequest().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

    @MessageMapping("/chatPrivate")
    public ResponseEntity<?> sendToUser(@Payload ChatMessage message) {
        try{
            userService.sendMessage(message);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }

//    @GetMapping("/oauth2/token")
//    public ResponseEntity<?> getUserInfo(
//            @RegisteredOAuth2AuthorizedClient("google") OAuth2AuthorizedClient authorizedClient) {
//        String accessToken = authorizedClient.getAccessToken().getTokenValue();
//        try{
//            String token = authenticationService.oauth2Token(accessToken);
//            if(token == null) {
//                return ResponseEntity.badRequest().build();
//            }
//            return ResponseEntity.ok(token);
//        } catch (Exception e) {
//            throw new ErrorException(e.getMessage());
//        }
//    }


    @GetMapping("/logout")
    public ResponseEntity<?> logout(){
        try{
            authenticationService.logout();
            return ResponseEntity.ok().build();
        }catch (Exception e) {
            throw new ErrorException(e.getMessage());
        }
    }
}
