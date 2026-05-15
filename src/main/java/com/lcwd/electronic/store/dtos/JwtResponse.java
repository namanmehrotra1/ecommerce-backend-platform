package com.lcwd.electronic.store.dtos;

import com.lcwd.electronic.store.entities.RefreshToken;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JwtResponse {
    private UserDto user;
    private String token;
    private RefreshTokenDto refreshToken;
}
