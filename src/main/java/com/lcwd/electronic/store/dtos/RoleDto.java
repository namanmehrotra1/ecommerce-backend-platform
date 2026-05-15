package com.lcwd.electronic.store.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
//@Data -> Includes
//Getters for all fields
//Setters for all non-final fields
//toString() method
//equals() and hashCode() methods based on fields
//A required-args constructor (a constructor for final fields and fields annotated with @NonNull)
public class RoleDto {
    private int roleId;
    private String name;
}
