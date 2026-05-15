package com.lcwd.electronic.store.services.implementations;

import com.lcwd.electronic.store.dtos.AppConstants;
import com.lcwd.electronic.store.dtos.PageableResponse;
import com.lcwd.electronic.store.dtos.ProductDto;
import com.lcwd.electronic.store.dtos.UserDto;
import com.lcwd.electronic.store.entities.Product;
import com.lcwd.electronic.store.entities.Role;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.exceptions.BadRequestException;
import com.lcwd.electronic.store.exceptions.DuplicateResourceException;
import com.lcwd.electronic.store.exceptions.ResourceNotFoundException;
import com.lcwd.electronic.store.helper.Helper;
import com.lcwd.electronic.store.repositories.RoleRepository;
import com.lcwd.electronic.store.repositories.UserRepository;
import com.lcwd.electronic.store.services.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("User with email '" + userDto.getEmail() + "' already exists", HttpStatus.BAD_REQUEST);
        }
//        If you have taken userID as String then here we'll generate the unique id in string format.
//        String userId = UUID.randomUUID().toString();
//        userDto.setId(userId);
        // Encoding password
        userDto.setPassword(passwordEncoder.encode(userDto.getPassword()));
        // DTO -> Entity
        User user = dtoToEntity(userDto); // mapper.map(userDto, User.class);
        // Assign NORMAL role to user
        // By default jo bhi api se user banega usko humlog NORMAL user banaenge
        // Get the NORMAL role
        Role roleNormal = roleRepository.findByName("ROLE_" + AppConstants.ROLE_NORMAL)
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_" + AppConstants.ROLE_NORMAL).build()));
        user.setRoles(List.of(roleNormal));
        User savedUser = userRepository.save(user);
        // Entity -> DTO
        UserDto userSaved = entityToDto(savedUser);
        logger.info("SAVED USER {} ", userSaved);
        return userSaved;
//        return userRepository.save(userDto); This requires User that is entity with annotations, so need to convert user DTO to User Entity
    }

    @Override
    public UserDto updateUser(UserDto userDto, int userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("The User with the given id : " + userID + " is not found", HttpStatus.NOT_FOUND));
        user.setName(userDto.getName());
        user.setAbout(userDto.getAbout());
        user.setEmail(userDto.getEmail());
        user.setGender(userDto.getGender());
        user.setImageName(userDto.getImageName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);
        UserDto updatedUser = entityToDto(savedUser);
        logger.info("UPDATED USER : {} ", updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(int userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("The User with the given id : " + userID + " is not found", HttpStatus.NOT_FOUND));
        //delete user
        logger.info("USER DELETED : {} ", user);
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDirection) {
        if (!sortDirection.equalsIgnoreCase("asc") && !sortDirection.equalsIgnoreCase("desc")) {
            throw new BadRequestException("Invalid Sort Direction : " + sortDirection + " . Allowed values are 'asc' or 'desc'.", HttpStatus.BAD_REQUEST);
        }
        // Sort logic
        Sort sort = (sortDirection.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        // Page Number default starts from 0
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<User> page = userRepository.findAll(pageable);
// MOVED TO HELPER CLASS
        //        List<User> users = page.getContent();
////        return userList.stream().map(user -> entityToDto(user)).toList();
//        List<UserDto> userDtoList = users.stream().map(this::entityToDto).toList();
//        logger.info("ALL USERS : {} ", userDtoList);
//        return PageableResponse
//                .<UserDto>builder()
//                .content(userDtoList)
//                .pageNumber(page.getNumber())
//                .totalElements(page.getTotalElements())
//                .pageSize(page.getSize())
//                .totalPages(page.getTotalPages())
//                .lastPage(page.isLast())
//                .build();
        logger.info("ALL USERS : {} ", page);
        return Helper.getPageableResponse(page, UserDto.class);
    }

    @Override
    public UserDto getSingleUserById(int userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("The User with the given id : " + userID + " is not found", HttpStatus.NOT_FOUND));
        logger.info("FETCHED USER BY ID : {} ", user);
        return entityToDto(user);
    }

    @Override
    public UserDto getSingleUserByEmail(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new ResourceNotFoundException("The User with given Email ID : " + userEmail + " is not found", HttpStatus.NOT_FOUND));
        UserDto userDto = entityToDto(user);
        logger.info("FETCHED USER BY EMAIL : {} ", user);
        return userDto;
    }

    @Override
    public List<UserDto> searchUser(String keyword) {
        List<User> byNameContaining = userRepository.findByNameContaining(keyword);
        List<UserDto> userDtoList = byNameContaining.stream().map(this::entityToDto).toList();
        logger.info("SEARCHED USERS LIST : {} ", userDtoList);
        return userDtoList;
    }

    @Override
    public void deleteAllUsers() {
        userRepository.deleteAll();
        logger.info("ALL USERS DELETED");
    }

    @Override
    public void deleteMultipleUsers(int[] userIDs) {
        List<Integer> usersList = Arrays.stream(userIDs).boxed().toList();
        userRepository.deleteAllById(usersList);
        logger.info("LIST OF USERS DELETED WITH IDs : {} ", userIDs);
    }

//    // For List<Integer>  -  @PathVariable if you want to use
//    @Override
//    public void deleteMultipleUsers(List<Integer> userIDs) {
//        userRepository.deleteAllById(userIDs);
//        logger.info("LIST OF USERS DELETED WITH IDs : {} ", userIDs);
//    }

    @Override
    public UserDto patchUpdate(UserDto userDto, int userID) {
        User user = userRepository.findById(userID).orElseThrow(() -> new ResourceNotFoundException("The User with the given id : " + userID + " is not found", HttpStatus.NOT_FOUND));
//        if (userDto.getName() != null) {
//            user.setName(userDto.getName());
//        }
//        if (userDto.getGender() != null) {
//            user.setGender(userDto.getGender());
//        }
//        if (userDto.getEmail() != null) {
//            user.setEmail(userDto.getEmail());
//        }
//        if (userDto.getAbout() != null) {
//            user.setAbout(user.getAbout());
//        }
//        if (userDto.getImageName() != null) {
//            user.setImageName(userDto.getImageName());
//        }
//        if (userDto.getPassword() != null) {
//            user.setPassword(userDto.getPassword());
//        }
        // OR SHORTENED------------>
//        Optional.ofNullable(userDto.getName()).ifPresent(user::setName);
//        Optional.ofNullable(userDto.getEmail()).ifPresent(user::setEmail);
//        Optional.ofNullable(userDto.getAbout()).ifPresent(user::setAbout);
//        Optional.ofNullable(userDto.getGender()).ifPresent(user::setGender);
//        Optional.ofNullable(userDto.getImageName()).ifPresent(user::setImageName);
//        Optional.ofNullable(userDto.getPassword()).ifPresent(user::setPassword);
        if (userDto.getName() != null) {
            String name = userDto.getName().trim();
            if (name.length() < 4 || name.length() > 20) {
                throw new BadRequestException("Name must be between 4 and 20 characters and not blank", HttpStatus.BAD_REQUEST);
            }
            userDto.setName(name);
        }

        if (userDto.getGender() != null) {
            String gender = userDto.getGender().trim();
            if (gender.length() < 2 || gender.length() > 6) {
                throw new BadRequestException("Gender must be between 2 and 6 characters and not blank", HttpStatus.BAD_REQUEST);
            }
            userDto.setGender(gender);
        }

        if (userDto.getEmail() != null) {
            String email = userDto.getEmail();
            if (!email.matches("^(?=[a-zA-Z0-9._%+-]*\\d)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
                throw new BadRequestException("Invalid email format", HttpStatus.BAD_REQUEST);
            }
            userDto.setEmail(email);
        }

        if (userDto.getPassword() != null) {
            String password = userDto.getPassword();
            if (password.length() < 3 || password.length() > 10) {
                throw new BadRequestException("Password must be between 3 and 10 characters", HttpStatus.BAD_REQUEST);
            }
            userDto.setPassword(password);
        }

        if (userDto.getAbout() != null) {
            String about = userDto.getAbout().trim();
            if (about.isEmpty()) {
                throw new BadRequestException("About cannot be blank", HttpStatus.BAD_REQUEST);
            }
            userDto.setAbout(about);
        }

        if (userDto.getImageName() != null) {
            if (!userDto.getImageName().matches("^.+\\.(jpeg|png)$")) {
                throw new BadRequestException("Invalid image name. Only .png and .jpeg files are allowed.", HttpStatus.BAD_REQUEST);
            }
        }
        logger.info("ALL FETCHED USERS : {} ", user);
        // OR MORE SHORTENED------------>
        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.typeMap(UserDto.class, User.class).addMappings(mapper -> mapper.skip(User::setId));
        mapper.map(userDto, user); // user and not User.class because User.class will create a new object and patch update doesn't create new object it just updates the existing one.
        return mapper.map(userRepository.save(user), UserDto.class);
    }

    @Override
    public UserDto updateUserRole(int userId, int roleId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("The User with the given id : " + userId + " is not found", HttpStatus.NOT_FOUND));
        Role role = roleRepository.findById(roleId).orElseThrow(() -> new ResourceNotFoundException("Role not found with ID" + roleId, HttpStatus.NOT_FOUND));
        user.setRoles(new ArrayList<>(List.of(role)));
        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    // Manual Method -> we have to create these methods in every service and to avoid that there are libraries that is ModelMapper
    private UserDto entityToDto(User savedUser) {
//        return UserDto.builder()
//                .id(savedUser.getId())
//                .name(savedUser.getName())
//                .email(savedUser.getEmail())
//                .gender(savedUser.getGender())
//                .password(savedUser.getPassword())
//                .imageName(savedUser.getImageName())
//                .about(savedUser.getAbout())
//                .build();

        return mapper.map(savedUser, UserDto.class);
    }

    // Manual Method
    private User dtoToEntity(UserDto userDto) {
//        return User.builder()
//                .id(userDto.getId())
//                .name(userDto.getName())
//                .email(userDto.getEmail())
//                .gender(userDto.getGender())
//                .password(userDto.getPassword())
//                .imageName(userDto.getImageName())
//                .about(userDto.getAbout())
//                .build();

        return mapper.map(userDto, User.class);
    }


}
