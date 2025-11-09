package com.Man10h.book_store.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @NotNull(message = "Required")
    private String username;

    @NotNull(message = "Required")
    private String password;
}
