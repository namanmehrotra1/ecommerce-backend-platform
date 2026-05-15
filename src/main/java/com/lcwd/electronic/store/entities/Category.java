package com.lcwd.electronic.store.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "JPA_CATEGORY")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@ToString
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "CATEGORY ID")
    private int iD;
    @Column(name = "CATEGORY_TITLE", nullable = false, length = 60, unique = true)
    private String title;
    @Column(name = "CATEGORY_DESCRIPTION", length = 100)
    private String description;
    @Column(name = "CATEGORY_COVER_IMAGE")
    private String coverImage;
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Product> products = new ArrayList<>();
}
