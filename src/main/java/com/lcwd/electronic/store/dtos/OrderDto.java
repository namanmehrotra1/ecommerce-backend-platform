package com.lcwd.electronic.store.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lcwd.electronic.store.entities.OrderItems;
import com.lcwd.electronic.store.entities.User;
import com.lcwd.electronic.store.validators.NotBlankAndSizeValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class OrderDto {
    private int orderId;
    private String orderStatus = "PENDING";
    @NotBlankAndSizeValidation(min = 4, max = 7, message = "Payment status must be PAID/NOT PAID")
    private String paymentStatus = "NOTPAID";
    @NotNull(message = "Order amount can't be null !!")
    private double orderAmount;
    @Column(length = 1000)
    @NotBlank(message = "Billing address is REQUIRED !!")
    private String billingAddress;
    @NotBlankAndSizeValidation(min = 5, max = 30, message = "Billing name must be in between 5 to 30 characters long !!")
    private String billingName;
    @NotBlankAndSizeValidation(min = 10, max = 10, message = "Billing phone number must of 10 digits !!")
    private String billingPhone;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date orderedDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date deliveredDate;
    //    private UserDto user;
    private List<OrderItemsDto> orderItems = new ArrayList<>();
}

