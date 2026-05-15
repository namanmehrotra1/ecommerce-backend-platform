package com.lcwd.electronic.store;

import com.lcwd.electronic.store.dtos.AppConstants;
import com.lcwd.electronic.store.entities.Role;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.repositories.RoleRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.List;

@SpringBootApplication
@EnableWebMvc
public class ElectronicStoreApplication{// implements CommandLineRunner {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(ElectronicStoreApplication.class, args);
    }

//    @Override
//    public void run(String... args) throws Exception {
////        Role roleAdmin = roleRepository.findByName("ROLE_" + AppConstants.ROLE_ADMIN).orElse(null);
//        Role roleAdmin = roleRepository.findByName("ROLE_"+AppConstants.ROLE_ADMIN).orElseGet(()->
//            roleRepository.save(Role.builder().name("ROLE_"+AppConstants.ROLE_ADMIN).build()));
////        Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL).orElse(null);
//        Role roleNormal = roleRepository.findByName("ROLE_"+AppConstants.ROLE_NORMAL).orElseGet(()->
//                roleRepository.save(Role.builder().name("ROLE_"+ AppConstants.ROLE_NORMAL).build()));
////        if (roleAdmin == null) {
////            roleAdmin = roleRepository.save(Role.builder()
////                    .name("ROLE_" + AppConstants.ROLE_ADMIN)
////                    .build());
////        }
////        if (roleNormal == null) {
////            roleNormal = roleRepository.save(Role.builder()
////                    .name("ROLE_" + AppConstants.ROLE_NORMAL)
////                    .build());
////        }
//
//        // ADMIN USER - - >
//        User user = userRepository.findByEmail("durgesh1@dev.in").orElseGet(()->{
//            List<Role> adminRoles = List.of(roleAdmin);
//            return userRepository.save(User.builder()
//                    .name("Durgesh")
//                    .email("durgesh1@dev.in")
//                    .password(passwordEncoder.encode("pass"))
//                    .roles(adminRoles)
//                    .gender("Male")
//                    .about("Teacher")
//                    .build());
//        });
//    }
}
//package com.lcwd.electronic.store;
//
//import com.lcwd.electronic.store.dtos.AppConstants;
//import com.lcwd.electronic.store.entities.Role;
//import com.lcwd.electronic.store.entities.User;
//import com.lcwd.electronic.store.repositories.RoleRepository;
//import com.lcwd.electronic.store.repositories.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//import java.util.List;

//@SpringBootApplication
//public class ElectronicStoreApplication implements CommandLineRunner {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public static void main(String[] args) {
//        SpringApplication.run(ElectronicStoreApplication.class, args);
//    }
//
//    @Override
//    public void run(String... args) {
//        Role roleAdmin = roleRepository.findByName("ROLE_" + AppConstants.ROLE_ADMIN)
//                .orElseGet(() -> roleRepository.save(Role.builder()
//                        .name("ROLE_" + AppConstants.ROLE_ADMIN)
//                        .build()));
//        if (roleAdmin == null) {
//            throw new IllegalStateException("roleAdmin is null!");
//        }
//
//        Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL)
//                .orElseGet(() -> roleRepository.save(Role.builder()
//                        .name("ROLE_" + AppConstants.ROLE_NORMAL)
//                        .build()));
//
//        userRepository.findByEmail("durgesh1@dev.in")
//                .orElseGet(() -> userRepository.save(User.builder()
//                        .name("Durgesh")
//                        .email("durgesh1@dev.in")
//                        .password(passwordEncoder.encode("pass"))
//                        .roles(List.of(roleAdmin))
//                        .gender("Male")
//                        .about("Teacher")
//                        .build()));
//    }
//}

