package com.lcwd.electronic.store.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lcwd.electronic.store.validators.NotBlankAndSizeValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString(exclude = "user")
@Table(name = "JPA_ORDER")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int orderId;

    // PENDING, DELIVERED, DISPATCHED
    // Can be done with enum also
    private String orderStatus;

    // Payment - NOT PAID, PAID
    // ENUM
    // boolean -> false -> NOT PAID || true-> PAID
    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;
    @Column(name = "ORDER_AMOUNT")
    private double orderAmount;
    @Column(length = 1000, name = "BILLING_ADDRESS")
    private String billingAddress;
    @Column(name = "BILLING_NAME")
    private String billingName;
    @Column(name = "BILLING_PHONE")
    private String billingPhone;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "ORDERED_DATE")
    private Date orderedDate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "DELIVERED_DATE")
    private Date deliveredDate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    private User user;
    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<OrderItems> orderItems = new ArrayList<>();
}
