package com.lcwd.electronic.store.dtos;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcwd.electronic.store.entities.Role;
import com.lcwd.electronic.store.validators.ImageNameValid;
import com.lcwd.electronic.store.validators.NotBlankAndSizeValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserDto {
    @Schema(description = "User ID", example = "123", accessMode = Schema.AccessMode.READ_ONLY) // Came from Swagger
    private int id;
    @Size(min = 4, max = 20, message = "Name is Required (Must be between 4 to 20 characters) !!!")
// Came from Validation
    @Schema(name = "User Name", description = "Name of the User", requiredMode = Schema.RequiredMode.REQUIRED, example = "Naman Mehrotra")
    private String name;
    @NotBlankAndSizeValidation(min = 4, max = 6, message = "Gender must be in between 2 to 6 characters (Male/Female) !!")
    private String gender;
    //    @Email(message = "Invalid User Email")
    //Pattern Matching
    @Pattern(regexp = "^(?=[a-zA-Z0-9._%+-]*\\d)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "User Email is Required and must be alphanumeric")
    private String email;
    @Size(min = 3, max = 10, message = "Password is Required to proceed and its length should be in between 3 to 10")
    private String password;
    @NotBlank(message = "Write something about yourself !!")
    private String about;

    private List<RoleDto> roles;

//    private CartDto cart;

    //    @Pattern
    //    Custom Validator
//    @Pattern(regexp = "^.+\\.(jpeg|png)$", message = "Only .jpeg and .png files are allowed")
    @ImageNameValid // This has @Pattern in it also
//    @NotBlank(message = "Image Name is blank, please input the value !!")
    private String imageName;
}
