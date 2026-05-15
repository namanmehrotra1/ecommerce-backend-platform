package com.lcwd.electronic.store.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "JPA_PRODUCTS")
@Builder
@ToString
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRODUCT_ID")
    private int productID;
    @Column(name = "PRODUCT_TITLE", nullable = false, unique = true, length = 60)
    private String title;
    @Column(name = "PRODUCT_DESCRIPTION", length = 10000)
    private String description;
    @Column(name = "PRODUCT_PRICE")
    private double price;
    @Column(name = "PRODUCT_DISCOUNTED_PRICE")
    private double discountedPrice;
    @Column(name = "PRODUCT_ADDED_DATE")
    private Date addedDate;
    @Column(name = "PRODUCT_LIVE_STATUS")
    private boolean liveStatus;
    @Column(name = "PRODUCT_QUANTITY")
    private int quantity;
    @Column(name = "PRODUCT_STOCK")
    private boolean stock;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

}
