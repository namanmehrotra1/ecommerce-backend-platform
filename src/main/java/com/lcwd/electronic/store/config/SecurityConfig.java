package com.lcwd.electronic.store.config;

import com.lcwd.electronic.store.dtos.AppConstants;
import com.lcwd.electronic.store.security.JwtAuthenticationEntryPoint;
import com.lcwd.electronic.store.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_URLS = {
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-resources/**",
            "/v3/api-docs/**",
            "/v2/api-docs/**"
    };
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        // Configurations
        // urls
        // public kaun se protected kaun se
        // Kaun se url admin, kaun se normal user

        // Configuring urls
//        security.cors(httpSecurityCorsConfigurer-> httpSecurityCorsConfigurer.disable());
//        security.cors(AbstractHttpConfigurer::disable);
        security.cors(httpSecurityCorsConfigurer -> httpSecurityCorsConfigurer.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration corsConfiguration = new CorsConfiguration();
                        // ORIGINS
                        // METHODS
//                        corsConfiguration.addAllowedOrigin("http://localhost:4200");
//                        corsConfiguration.setAllowedOrigins(List.of("http://localhost:4200","http://localhost:4300","http://localhost:3000"));
                        corsConfiguration.setAllowedOriginPatterns(List.of("*"));
                        corsConfiguration.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
                        corsConfiguration.setAllowCredentials(true);
                        corsConfiguration.setAllowedHeaders(List.of("Authorization","Content-Type","Accept"));
                        corsConfiguration.setExposedHeaders(List.of("Authorization"));
                        corsConfiguration.setMaxAge(3000L);
                        return corsConfiguration;
                    }
                }))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request ->

                        request.requestMatchers(PUBLIC_URLS).permitAll()

// Auth-related: Public endpoints
                                .requestMatchers(HttpMethod.POST, "/auth/generate-token", "/auth/regenerate-token").permitAll()
// Other /auth endpoints require authentication
                                .requestMatchers("/auth/**").authenticated()

// User-related
                                .requestMatchers(HttpMethod.POST, "/users/**").permitAll() // Register new user
                                .requestMatchers(HttpMethod.GET, "/users/**").permitAll()  // Get user info (e.g., profile view)
                                .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole(AppConstants.ROLE_ADMIN, AppConstants.ROLE_NORMAL)
                                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(AppConstants.ROLE_ADMIN)

// Product-related
                                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/products/**").hasRole(AppConstants.ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, "/products/**").hasRole(AppConstants.ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, "/products/**").hasRole(AppConstants.ROLE_ADMIN)

// Category-related
                                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/categories/**").hasRole(AppConstants.ROLE_ADMIN)
                                .requestMatchers(HttpMethod.PUT, "/categories/**").hasRole(AppConstants.ROLE_ADMIN)
                                .requestMatchers(HttpMethod.DELETE, "/categories/**").hasRole(AppConstants.ROLE_ADMIN)

// Any other request
                                .anyRequest().permitAll())

//                        requestMatchers(PUBLIC_URLS).permitAll()
//                                .requestMatchers(HttpMethod.PUT, "/users/**").hasAnyRole(AppConstants.ROLE_ADMIN, AppConstants.ROLE_NORMAL)
//                                .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole(AppConstants.ROLE_ADMIN)
//                                .requestMatchers(HttpMethod.GET, "/products/**").permitAll()
//                                .requestMatchers("/products/**").hasRole(AppConstants.ROLE_ADMIN)
//                                .requestMatchers(HttpMethod.GET, "/users/**").permitAll()
//                                .requestMatchers(HttpMethod.POST, "/users/**").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/categories/**").permitAll()
//                                .requestMatchers("/categories/**").hasRole(AppConstants.ROLE_ADMIN)
//                                .requestMatchers(HttpMethod.POST, "/auth/generate-token", "/auth/regenerate-token").permitAll()
//                                .requestMatchers("/auth/**").authenticated()
//                                .anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
//                .httpBasic(Customizer.withDefaults());
                // Entry Point
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Session creation Policy
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Main -->
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        // THE REQUEST MATCHERS WILL BE CHECKED ONE BY ONE FROM THE START SO GIVE THE REQUESTS ACCORDINGLY
        return security.build();
//        return security.authorizeHttpRequests(request ->
//                request.requestMatchers("/products/**").authenticated()
//                        .anyRequest().permitAll()).build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
