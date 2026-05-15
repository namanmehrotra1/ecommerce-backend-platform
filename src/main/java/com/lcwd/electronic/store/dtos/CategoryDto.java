package com.lcwd.electronic.store.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcwd.electronic.store.validators.ImageNameValid;
import com.lcwd.electronic.store.validators.NotBlankAndSizeValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonPropertyOrder({"id", "title", "description", "coverImage"})
public class CategoryDto {
    private int iD;
    @NotBlankAndSizeValidation(max = 40, message = "Title must be of minimum 4 and maximum 40 characters")
    private String title;
    @Size(min = 10, max = 100, message = "Description can't be blank, must be above 10 characters long")
    private String description;
    @ImageNameValid
    private String coverImage;
//    private List<ProductDto> products = new ArrayList<>();
}
