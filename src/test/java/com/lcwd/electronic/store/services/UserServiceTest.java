package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.AppConstants;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.RoleDto;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.Role;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.repositories.RoleRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.implementations.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserServiceTest {

    User user;
    Role role;

    @MockBean
    private UserRepository userRepository;
    @MockBean
    private RoleRepository roleRepository;
    //    @Autowired
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void init() {

        role = Role.builder()
                .name(AppConstants.ROLE_NORMAL)
                .build();
        user = User.builder()
                .name("Durgesh")
                .about("This is testing create method")
                .email("durgesh1@dev.in")
                .password("abcd")
                .gender("Male")
                .imageName("durgesh.png")
                .roles(List.of(role))
                .build();
    }

    // CREATE
    @Test
    public void createUserTest() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL)).thenReturn(Optional.of(role));
        UserDto createdUser = userService.createUser(modelMapper.map(user, UserDto.class));
        System.out.println(createdUser.getName());
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals("Durgesh", createdUser.getName());
    }

    // UPDATE
    @Test
    public void updateUser() {
        int userId = 1;
        UserDto userDto = UserDto.builder()
                .name("Durgesh K Tiwari")
                .about("About Me")
                .email("durgesh11@gmail.com")
                .gender("Male")
                .password("lcwd")
                .imageName("xyz.png")
                .roles(List.of(modelMapper.map(role, RoleDto.class)))
                .build();

        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        UserDto updatedUser = userService.updateUser(userDto, userId);
        System.out.println(updatedUser.getName());
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals("Durgesh K Tiwari", updatedUser.getName());
        Assertions.assertTrue(passwordEncoder.matches("lcwd", updatedUser.getPassword()));
        Assertions.assertEquals(userDto.getEmail(), updatedUser.getEmail());
    }

    @Test
    public void deleteUserTest() {
        int userId = 1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        userService.deleteUser(userId);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
        System.out.println("Deleted User : " + user);
    }

    @Test
    public void getAllUsersTest() {
        User user1 = User.builder()
                .name("Ankit")
                .about("This is testing create method")
                .email("durgesh1@dev.in")
                .password("abcd")
                .gender("Male")
                .imageName("durgesh.png")
                .roles(List.of(role))
                .build();

        User user2 = User.builder()
                .name("Uttam")
                .about("This is testing create method")
                .email("durgesh1@dev.in")
                .password("abcd")
                .gender("Male")
                .imageName("durgesh.png")
                .roles(List.of(role))
                .build();
        List<User> userList = Arrays.asList(user, user1, user2);
        Page<User> page = new PageImpl<>(userList);
        Mockito.when(userRepository.findAll((Pageable) Mockito.any())).thenReturn(page);
        PageableResponse<UserDto> allUsers = userService.getAllUsers(1, 2, "name", "asc");
        Assertions.assertEquals(3, allUsers.getContent().size());
    }

    @Test
    public void getSingleUserByIdTest() {
        int userId = 1;
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        UserDto singleUserById = userService.getSingleUserById(userId);
        Assertions.assertEquals(user.getName(), singleUserById.getName(), "Name Not Matched !!");
    }

    @Test
    public void getSingleUserByEmailTest() {
        String email = "durgesh1@dev.in";
        Mockito.when(userRepository.findByEmail(Mockito.any())).thenReturn(Optional.ofNullable(user));
        UserDto singleUserByEmail = userService.getSingleUserByEmail(email/*user.getEmail()*/);
        Assertions.assertEquals("durgesh1@dev.in"/*user.getEmail()*/, singleUserByEmail.getEmail(), "Email Not Matched !!");
    }

    @Test
    public void searchUserTest() {
        String keyword = "Kumar";
        User user1 = User.builder()
                .name("Ankit Kumar")
                .about("This is testing create method")
                .email("durgesh1@dev.in")
                .password("abcd")
                .gender("Male")
                .imageName("durgesh.png")
                .roles(List.of(role))
                .build();

        User user2 = User.builder()
                .name("Uttam Kumar Tiwari")
                .about("This is testing create method")
                .email("durgesh1@dev.in")
                .password("abcd")
                .gender("Male")
                .imageName("durgesh.png")
                .roles(List.of(role))
                .build();
        List<User> users = Arrays.asList(user, user1, user2);
        Mockito.when(userRepository.findByNameContaining(Mockito.any())).thenReturn(users);
        List<UserDto> searchedUser = userService.searchUser(keyword);
        Assertions.assertNotNull(searchedUser);
        Assertions.assertEquals(3, searchedUser.size());
        Assertions.assertEquals("Durgesh", searchedUser.get(0).getName());
        Assertions.assertEquals("Ankit Kumar", searchedUser.get(1).getName());
        Assertions.assertEquals("Uttam Kumar Tiwari", searchedUser.get(2).getName());
    }

    @Test
    public void deleteMultipleUsersTest() {
        int[] userIds = {1, 2, 3};
        List<Integer> userIdList = Arrays.stream(userIds).boxed().toList();
        Mockito.doNothing().when(userRepository).deleteAllById(userIdList);
        userService.deleteMultipleUsers(userIds);
        Mockito.verify(userRepository, Mockito.times(1)).deleteAllById(userIdList);
    }

    @Test
    public void patchUpdateTest() {
        int userId = 1;
        UserDto patchData = UserDto.builder()
                .name("Durgesh K Tiwari")
                .email("durgesh01@gmail.com")
                .build();
        Mockito.when(userRepository.findById(Mockito.any())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(user);
        UserDto updatedUser = userService.patchUpdate(patchData, userId);
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals("Durgesh K Tiwari", updatedUser.getName(), "Name not Mathed !!");
        Assertions.assertEquals("durgesh01@gmail.com", updatedUser.getEmail(), "Email Not Matched !!");
    }
}
