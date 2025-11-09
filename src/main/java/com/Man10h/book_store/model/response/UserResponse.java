package com.Man10h.book_store.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private String roleName;
    private Long cartId;

    public UserResponse(Long id, String username, String email, Boolean enabled, String roleName) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.enabled = enabled;
        this.roleName = roleName;
    }
}
