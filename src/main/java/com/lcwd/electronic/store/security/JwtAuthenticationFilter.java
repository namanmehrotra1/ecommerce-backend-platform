package com.lcwd.electronic.store.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // api se pehle chalega jwt header ko verify karne ke liye

        // Authorization : Bearer dygayusgdyhjasdg
        String requestHeader = request.getHeader("Authorization");
        logger.info("Header : " + requestHeader);

        String username = null;
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            // everything looks good : process
            token = requestHeader.substring(7);
            try {
                username = jwtHelper.getUsernameFromToken(token);
                logger.info("Token Username : {}", username);
            } catch (IllegalArgumentException ex) {
                logger.error("Illegal Argument while fetching the username !! " + ex.getMessage());
                ex.printStackTrace();
            } catch (ExpiredJwtException ex) {
                logger.error("Given JWT is expired !! " + ex.getMessage());
            } catch (MalformedJwtException ex) {
                logger.error("Some change has been done in token !! INVALID TOKEN " + ex.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            logger.error("Invalid Header !! Header is not starting with Bearer");
        }
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // username me kuch hai
            // authentication null
            // So give authentication
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // VALIDATE TOKEN
            if (username.equals(userDetails.getUsername()) && !jwtHelper.isTokenExpired(token)) {
                // Token Valid
                // Security context ke andar authentication set karenge
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}
