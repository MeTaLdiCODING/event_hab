package com.event.hab.auth.servise;

import com.event.hab.auth.DTO.LoginRequest;
import com.event.hab.auth.DTO.RegisterRequest;
import com.event.hab.auth.model.User;
import com.event.hab.auth.model.UserRole;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.common.castomException.InvalidCredentialsException;
import com.event.hab.common.castomException.UserAlreadyExistsException;
import com.event.hab.common.castomException.UserNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class AuthService {
    final UserRepository userRepository;
    final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;


    public AuthService(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(RegisterRequest request){
        if(userRepository.findByEmail(request.getEmail()).isPresent()){
           throw new UserAlreadyExistsException();
       }
       else {
           User user = new User();
           user.setEmail(request.getEmail());
           user.setFullName(request.getFullName());
           user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
           user.setUserRole(UserRole.PARTICIPANT);
           userRepository.save(user);
           return "Пользователь зарегестрирован";
       }
    }

    public String login(LoginRequest request){
        Optional<User> optionalUser = userRepository.findByEmail(request.getEmail());
        if(optionalUser.isEmpty()){
            throw new UserNotFoundException();
        }
        else {
            User user = optionalUser.get();
            boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
            if (!isMatch){
                throw new InvalidCredentialsException();
            }
            else {
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getUserRole().name())
                        .build();

                return jwtService.generateToken(userDetails);

            }
        }

    }

}
