package com.Man10h.book_store.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull(message = "Required")
    private String username;

    @NotNull(message = "Required")
    private String password;

    @NotNull(message = "Required")
    private String email;
}
