package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.*;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.security.JwtHelper;
import com.lcwd.electronic.store.services.RefreshTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "AuthController", description = "APIs for Authentication")
public class AuthenticationController {
    Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    // method to generate token
    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest jwtRequest) {
        logger.info("Email : {} , Password : {} ", jwtRequest.getEmail(), jwtRequest.getPassword());
        this.doAuthenticate(jwtRequest.getEmail(), jwtRequest.getPassword());
        User userDetails = (User) userDetailsService.loadUserByUsername(jwtRequest.getEmail());
        // Generate Token
        String generatedToken = jwtHelper.generateToken(userDetails);
        // Refresh Token
        RefreshTokenDto refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        // Sending the response
        JwtResponse response = JwtResponse.builder()
                .token(generatedToken)
                .user(modelMapper.map(userDetails, UserDto.class))
                .refreshToken(refreshToken)
                .build();
        return ResponseEntity.ok(response);
    }

    private void doAuthenticate(String email, String password) {
        try {
            Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
            authenticationManager.authenticate(authentication);

        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid Credentials : Email and Password !!");
        }
    }

    //    private void doAuthenticate(@NotBlank(message = "Email can't be blank !!") String email, @NotBlank(message = "Password can't be blank !!") String password) {
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadCredentialsException("Email not found !!"));
//        if (!passwordEncoder.matches(password, user.getPassword())) {
//            throw new BadCredentialsException("Invalid Password !!");
//        }
//        Authentication authentication = new UsernamePasswordAuthenticationToken(email, password);
//        authenticationManager.authenticate(authentication);
//    }
    @PostMapping("/regenerate-token")
    public ResponseEntity<JwtResponse> regenerateToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenDto refreshTokenDto = refreshTokenService.findByToken(request.getRefreshToken());
        RefreshTokenDto verifiedRefreshToken = refreshTokenService.verifyRefreshToken(refreshTokenDto);
        UserDto user = refreshTokenService.getUser(verifiedRefreshToken);
        String generatedJwtToken = jwtHelper.generateToken(modelMapper.map(user, User.class));
        JwtResponse response = JwtResponse.builder()
                .token(generatedJwtToken)
                .refreshToken(verifiedRefreshToken)
                .user(user)
                .build();
        return ResponseEntity.ok(response);

    }
}
