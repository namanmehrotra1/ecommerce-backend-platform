package com.lcwd.electronic.store.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class JwtRequest {
    @NotBlank(message = "Username can't be blank !!")
    private String email;
    @NotBlank(message = "Password can't be blank !!")
    private String password;
}
