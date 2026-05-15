package com.lcwd.electronic.store.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "JPA_USER")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString//(exclude = {"roles","orders"})
public class User implements UserDetails {

    // ROLES - ADMIN, NORMAL

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "USER_ID")
    private int id;
    // To remove the id jumps by 50 due to AUTO use the below
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
//    @SequenceGenerator(name = "user_seq", sequenceName = "user_sequence", allocationSize = 1)
//    @Column(name = "USER_ID")
//    private int id;
    @Column(name = "USER_NAME")
    private String name;
    @Column(name = "USER_GENDER")
    private String gender;
    @Column(name = "USER_EMAIL", unique = true)
    private String email;
    @Column(name = "USER_PASSWORD", length = 500)
    private String password;
    @Column(name = "ABOUT", length = 500)
    private String about;
    @Column(name = "USER_IMAGE_NAME")
    private String imageName;
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    // Must be implemented
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

    // IMPORTANT
    @Override
    public String getUsername() {
        return this.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;//UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;//UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;// UserDetails.super.isCredentialsNonExpired();
    }

    // IMPORTANT
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isEnabled() {
        return true;//UserDetails.super.isEnabled();
    }
}
