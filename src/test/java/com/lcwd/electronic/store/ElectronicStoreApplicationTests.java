package com.lcwd.electronic.store;

import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.security.JwtHelper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ElectronicStoreApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Test
    void contextLoads() {
    }

    @Test
    void TestTokens() {
        User user = userRepository.findByEmail("durgesh1@dev.in").get();
        String token = jwtHelper.generateToken(user);
        System.out.println(token);
        System.out.println("Getting username from token : " + jwtHelper.getUsernameFromToken(token));
        System.out.println("Token Expired : " + jwtHelper.isTokenExpired(token));
    }

}
