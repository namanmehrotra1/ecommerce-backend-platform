package com.lcwd.electronic.store.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.lcwd.electronic.store.entities.Category;
import com.lcwd.electronic.store.validators.NotBlankAndSizeValidation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonPropertyOrder({"productID", "title", "description", "price", "addedDate", "liveStatus", "stock"})
public class ProductDto {
    private int productID;
    @NotBlankAndSizeValidation(min = 10, max = 80, message = "Title must be 10 to 80 characters long")
    private String title;
    @NotBlankAndSizeValidation(min = 10, message = "Description must be above 10 characters long")
    private String description;
    @NotNull(message = "Price is Required !!")
    private Double price;
    private Double discountedPrice;
    @NotNull(message = "Quantity can't be blank")
    private Integer quantity;
    @NotNull(message = "Added date is Required !!")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date addedDate;
    @NotNull(message = "Live Status is Required !!")
    private Boolean liveStatus;
    @NotNull(message = "Present Stock value is Required !!")
    private Boolean stock;
    private CategoryDto category;
}
