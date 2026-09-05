package com.event.hab.auth;

import com.event.hab.auth.model.User;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.auth.servise.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    final JwtService jwtService;
    final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal
            (HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

       String header =  request.getHeader("Authorization");
       if (header==null || !header.startsWith("Bearer ")){
           filterChain.doFilter(request, response);
           return;
       }
        String token = header.substring(7);
       String email;
       try {
           email = jwtService.extractEmail(token);
       }catch (Exception e){
           filterChain.doFilter(request, response);
           return;
       }


        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()){
            filterChain.doFilter(request, response);
            return;
        };
        User user = optionalUser.get();
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getUserRole().name())
                .build();
        try {
            if (!jwtService.validateToken(token, userDetails)){
                filterChain.doFilter(request, response);
                return;
            }
        }catch (Exception e){
            filterChain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authToken = UsernamePasswordAuthenticationToken
                .authenticated(userDetails,null,userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);

    }



}
