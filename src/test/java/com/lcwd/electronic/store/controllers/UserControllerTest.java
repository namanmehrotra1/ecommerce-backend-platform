package com.lcwd.electronic.store.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lcwd.electronic.store.dtos.AppConstants;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.RoleDto;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.Role;
import com.lcwd.electronic.store.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @MockBean
    private UserService userService;

    private UserDto userDto;

    private RoleDto roleDto;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        roleDto = RoleDto.builder()
                .name(AppConstants.ROLE_NORMAL)
                .build();
        userDto = UserDto.builder()
                .name("Durgesh")
                .about("This is testing create method")
                .email("durgesh1@dev.in")
                .password("abcd")
                .gender("Male")
                .imageName("durgesh.png")
                .roles(List.of(roleDto))
                .build();
    }

    @Test
    public void createUserTest() throws Exception {
        // .users + POST + user data as json
        // data as json + status created
//        UserDto userDto = modelMapper.map(user, UserDto.class);
        Mockito.when(userService.createUser(Mockito.any())).thenReturn(userDto);
        // Actual Request for url
        this.mockMvc.perform(
                        MockMvcRequestBuilders.post("/users/create-user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(convertObjectToJsonString(userDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").exists())// $ -> represents whole json
                .andExpect(jsonPath("$.name").value("Durgesh"))
                .andExpect(jsonPath("$.email").value("durgesh1@dev.in"));
    }

    private String convertObjectToJsonString(Object userDto) throws JsonProcessingException {
        try {
            return new ObjectMapper().writeValueAsString(userDto);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Test
    public void updateUserTest() throws Exception {
        int userId = 1;
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.anyInt())).thenReturn(userDto);
        this.mockMvc.perform(
                        MockMvcRequestBuilders.put("/users/update-user/" + userId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJuYW1hbjFAZ21haWwuY29tIiwiaWF0IjoxNzU5MjMzNjY5LCJleHAiOjE3NTkyNTE2Njl9.zeycKmmF-NWYwNLQnJO03-CrXwVv1U2LjZ4xMIlpO1EJivav9QFIa1rPEwmay5DJvXP6Rq2GVYO-FJDuMaLWUA")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(convertObjectToJsonString(userDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    public void patchUpdateTest() throws Exception {
        int userId = 1;
        Mockito.when(userService.patchUpdate(Mockito.any(), Mockito.anyInt())).thenReturn(userDto);
        this.mockMvc.perform(
                        MockMvcRequestBuilders.patch("/users/patch-update-user/" + userId)
                                .header(HttpHeaders.AUTHORIZATION, "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJkdXJnZXNoMUBkZXYuaW4iLCJpYXQiOjE3NDc3ODc1MDcsImV4cCI6MTc0NzgwNTUwN30.BpZVxpnv2NjudTulKDZji4H0T5EM5WIfJvo5ztcGQrLRUC65sGP5BMOD-aStDhblxjbIa9lAS6p9Q3qEkrn1qA")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(convertObjectToJsonString(userDto))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.about").exists());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        UserDto userDto1 = UserDto.builder().name("durgesh").email("durgesh1@dev.in").password("abcd").about("Developer").build();
        UserDto userDto2 = UserDto.builder().name("rohit").email("rohit1@dev.in").password("abcd").about("Developer").build();
        UserDto userDto3 = UserDto.builder().name("ankit").email("ankit@dev.in").password("abcd").about("Developer").build();
        UserDto userDto4 = UserDto.builder().name("uttam").email("uttam@dev.in").password("abcd").about("Developer").build();
        UserDto userDto5 = UserDto.builder().name("rajiv").email("rajiv1@dev.in").password("abcd").about("Developer").build();
        PageableResponse<UserDto> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(Arrays.asList(userDto1, userDto2, userDto5, userDto3, userDto4));
        pageableResponse.setLastPage(false);
        pageableResponse.setPageNumber(100);
        pageableResponse.setPageSize(10);
        pageableResponse.setTotalPages(1000);
        Mockito.when(userService.getAllUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString())).thenReturn(pageableResponse);
        this.mockMvc.perform(
                        MockMvcRequestBuilders.get("/users/getUsers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(convertObjectToJsonString(pageableResponse))
                                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
}