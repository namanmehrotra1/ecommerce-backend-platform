package com.lcwd.electronic.store.services.implementations;

import com.lcwd.electronic.store.dtos.RefreshTokenDto;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.RefreshToken;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.repositories.RefreshTokenRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.RefreshTokenService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private ModelMapper modelMapper;

    // CONSTRUCTOR INJECTION BELOW ---->
    @Autowired // By default, not recommended as it injects automatically via constructor injection
    public RefreshTokenServiceImpl(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RefreshTokenDto createRefreshToken(String username) { // username is email only
        User user = userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("No user found with this email : " + username, HttpStatus.NOT_FOUND));
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(existing->{
                    existing.setToken(UUID.randomUUID().toString());
                    existing.setExpiryDate(Instant.now().plusSeconds(5*24*60*60));
                    return existing;
                })
                .orElseGet(()-> RefreshToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiryDate(Instant.now().plusSeconds(5*24*60*60))
                        .build());
//        if (refreshToken == null) {
//            refreshToken = RefreshToken.builder()
//                    .user(user)
//                    .token(UUID.randomUUID().toString())
//                    .expiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60))
//                    .build();
//        } else {
//            refreshToken.setToken(UUID.randomUUID().toString());
//            refreshToken.setExpiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60));
//        }
        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
        return this.modelMapper.map(savedRefreshToken, RefreshTokenDto.class);
    }

    @Override
    public RefreshTokenDto findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new ResourceNotFoundException("This " + token + " Refresh Token not found in database", HttpStatus.NOT_FOUND));
        return this.modelMapper.map(refreshToken, RefreshTokenDto.class);
    }

    @Override
    public RefreshTokenDto verifyRefreshToken(RefreshTokenDto refreshTokenDto) {
//        if (refreshTokenDto.getExpiryDate().compareTo(Instant.now()) < 0) {
//            refreshTokenRepository.delete(modelMapper.map(refreshTokenDto, RefreshToken.class));
//            throw new RuntimeException("Refresh Token Expired !!");
//        }
//        !!!!!!Explanation for the above if statement!!!!!!
//        compareTo is used to compare two objects that are Comparable (like Instant, Date, String, etc.).
//        It returns:
//        0 → if both are equal
//        a positive number → if the left side is after/greater than the right side
//        a negative number → if the left side is before/less than the right side
        if(refreshTokenDto.getExpiryDate().isBefore(Instant.now())){
            refreshTokenRepository.delete(modelMapper.map(refreshTokenDto, RefreshToken.class));
            throw new RuntimeException("REFRESH TOKEN IS EXPIRED!! \n Kindly generate a new refresh token!!");
        }
        return refreshTokenDto;
    }

    @Override
    public UserDto getUser(RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenDto.getToken()).orElseThrow(() -> new ResourceNotFoundException("Token not found !!", HttpStatus.NOT_FOUND));
        User user = refreshToken.getUser();
        return modelMapper.map(user, UserDto.class);
    }
}
