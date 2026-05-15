package com.lcwd.electronic.store.controllers;

import com.lcwd.electronic.store.dtos.ApiResponseMessage;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "UserController", description = "REST APIs to perform User related operations !!")
public class UserController {

    Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    //    CONSTRUCTOR INJECTION
//    private UserService userService; // Can also just create instance here and use @RequiredArgsConstructor to reduce the below boilerplate code
//    @Autowired // Usage of Autowired is not required when there's only one constructor in the class
//    public UserController(UserService userService) {
//        this.userService = userService;
//    }

    // CREATE USER
    @PostMapping("/create-user") // Apply @Valid wherever the data is being accepted
    @Operation(summary = "Create User", description = "Create a new User")
    @ApiResponse(responseCode = "200", description = "User created Successfully")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("CREATING USER");
        UserDto user = userService.createUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    // UPDATE USER
    @PutMapping("/update-user/{userID}")
    public ResponseEntity<UserDto> updateUser(@Valid @RequestBody UserDto userDto, @PathVariable int userID) {
        logger.info("UPDATING USER");
        UserDto updatedUser = userService.updateUser(userDto, userID);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    // PARTIAL UPDATE USER (PATCH UPDATE)
    @PatchMapping("/patch-update-user/{userID}")
    public ResponseEntity<UserDto> patchUpdateUser(@RequestBody UserDto userDto, @PathVariable int userID) {
        logger.info("PATCH UPDATING USER");
        UserDto patchedUpdate = userService.patchUpdate(userDto, userID);
        return new ResponseEntity<>(patchedUpdate, HttpStatus.OK);
    }

    // DELETE USER
    @DeleteMapping("/delete-user/{userID}")
    public ResponseEntity<ApiResponseMessage> deleteUser(@PathVariable int userID) {
        logger.info("DELETING USER.......");
        userService.deleteUser(userID);
        ApiResponseMessage message = ApiResponseMessage
                .builder()
                .message("User with ID : " + userID + " is deleted successfully")
                .success(true)
                .status(HttpStatus.OK).build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // DELETE MULTIPLE USERS
    @DeleteMapping("/delete-multiple-users")
    public ResponseEntity<ApiResponseMessage> deleteMultipleUsers(@RequestParam int[] userIDs) {
        logger.info("DELETING MULTIPLE USERS.......");
        userService.deleteMultipleUsers(userIDs);
        ApiResponseMessage message = ApiResponseMessage
                .builder()
                .message("Users with IDs : " + Arrays.toString(userIDs) + " are deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

//    // DELETE MULTIPLE USERS
//    @DeleteMapping("/delete-multiple-users")
//    public ResponseEntity<ApiResponseMessage> deleteMultipleUsers(@PathVariable List<Integer> userIDs) {
//        logger.info("DELETING MULTIPLE USERS.......");
//        userService.deleteMultipleUsers(userIDs);
//        ApiResponseMessage message = ApiResponseMessage
//                .builder()
//                .message("Users with IDs : " + Arrays.toString(userIDs) + " are deleted successfully")
//                .status(HttpStatus.OK)
//                .success(true)
//                .build();
//        return new ResponseEntity<>(message, HttpStatus.OK);
//    }

    // DELETE ALL USERS
    @DeleteMapping("/delete-users")
    public ResponseEntity<ApiResponseMessage> deleteAllUsers() {
        logger.info("DELETING ALL USERS.......");
        userService.deleteAllUsers();
        ApiResponseMessage message = ApiResponseMessage
                .builder()
                .message("All Users deleted successfully")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    // GET ALL USER
    @GetMapping("/getUsers")
    @Operation(summary = "Get All Users", description = "Fetches a list of all Users", tags = {"user-controller", "user apis"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "401", description = "You are not authorized to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<PageableResponse<UserDto>> getAllUsers(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "name", required = false) String sortBy,
            @RequestParam(value = "sortDirection", defaultValue = "asc", required = false) String sortDirection) {
        logger.info("FETCHING ALL USERS.......");
        PageableResponse<UserDto> allUsers = userService.getAllUsers(pageNumber, pageSize, sortBy, sortDirection);
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    // GET SINGLE USER BY ID
    @GetMapping("/get-user-by-id/{userID}")
    @Operation(summary = "Get Single User By ID !!", description = "Fetches Single User by ID !!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the requested User details "),
            @ApiResponse(responseCode = "401", description = "You are not Authorised to view the resource"),
            @ApiResponse(responseCode = "403", description = "Accessing the resource you were trying to reach is forbidden"),
            @ApiResponse(responseCode = "404", description = "The resource you were trying to reach is not found")
    })
    public ResponseEntity<UserDto> getUserById(@PathVariable("userID") int userID) {
        logger.info("FETCHING USER BY ID.......");
        UserDto singleUserById = userService.getSingleUserById(userID);
        return new ResponseEntity<>(singleUserById, HttpStatus.OK);
    }

    // GET SINGLE USER BY EMAIL
    @GetMapping("/get-user-by-email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable("email") String userEmail) {
        logger.info("FETCHING USER BY EMAIL.......");
        UserDto singleUserByEmail = userService.getSingleUserByEmail(userEmail);
        return new ResponseEntity<>(singleUserByEmail, HttpStatus.OK);
    }

    // SEARCH USER
    @GetMapping("/search-user/{keywords}")
    public ResponseEntity<List<UserDto>> searchUsers(@PathVariable String keywords) {
        logger.info("SEARCHING USERS......");
        List<UserDto> searchedUser = userService.searchUser(keywords);
        return new ResponseEntity<>(searchedUser, HttpStatus.OK);
    }

    @PutMapping("/{userId}/role/{roleId}")
    public ResponseEntity<UserDto> updateUserRole(@PathVariable int userId, @PathVariable int roleId) {
        UserDto updatedUserRole = userService.updateUserRole(userId, roleId);
        return new ResponseEntity<>(updatedUserRole, HttpStatus.OK);
    }

}
