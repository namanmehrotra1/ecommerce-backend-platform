package com.lcwd.electronic.store.services;

import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.UserDto;

import java.util.List;

public interface UserService {
    //create
    UserDto createUser(UserDto userDto);  // Now this user is being used for creating the table also and for transporting the data through layers also, that is Controller->Service->Repo which must be avoided, therefore DTOs(Data Transfer Objects) are used.

    //update
    UserDto updateUser(UserDto userDto, int userID);

    //delete
    void deleteUser(int userID);

    //get all users
    PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDirection);

    //get single user by ID
    UserDto getSingleUserById(int userID);

    //get single user by email
    UserDto getSingleUserByEmail(String userEmail);

    //search user
    List<UserDto> searchUser(String keyword);

    //delete all users
    void deleteAllUsers();

    //delete multiple users
    void deleteMultipleUsers(int[] userIDs);

//    void deleteMultipleUsers(List<Integer> userIDs);  If @PathVariable is what you want to use

    //partially update user
    UserDto patchUpdate(UserDto userDto, int userID);

    UserDto updateUserRole(int userId, int roleId);

    //other user specific features
}
