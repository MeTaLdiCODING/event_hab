package com.event.hab.auth.Servise;

import com.event.hab.auth.DTO.LoginRequest;
import com.event.hab.auth.DTO.RegisterRequest;
import com.event.hab.auth.Model.User;
import com.event.hab.auth.Model.UserRole;
import com.event.hab.auth.Repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service

public class AuthService {
    final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public AuthService(BCryptPasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequest request){
        //если пользователь стаким email найден
       if(!userRepository.findByEmail(request.getEmail()).isEmpty()){
           throw new IllegalArgumentException("Пользователь с таким email уже существует");
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
            throw new IllegalArgumentException("Пользователь с таким email не найден!");
        }
        else {
            User user = optionalUser.get();
            boolean isMatch = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());
            if (!isMatch){
                throw new IllegalArgumentException("Не верный пароль!");
            }
            else {
                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPasswordHash())
                        .roles(user.getUserRole().name())
                        .build();


                return "Выполняется вход...";
            }
        }

    }

}
