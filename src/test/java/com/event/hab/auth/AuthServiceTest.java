package com.event.hab.auth;

import com.event.hab.auth.DTO.LoginRequest;
import com.event.hab.auth.DTO.RegisterRequest;
import com.event.hab.auth.model.User;
import com.event.hab.auth.model.UserRole;
import com.event.hab.auth.repository.UserRepository;
import com.event.hab.auth.servise.AuthService;
import com.event.hab.auth.servise.JwtService;
import com.event.hab.common.castomException.InvalidCredentialsException;
import com.event.hab.common.castomException.UserAlreadyExistsException;
import com.event.hab.common.castomException.UserNotFoundException;
import com.event.hab.profile.service.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private AuthService authService;

    @Test
    void testLogin_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        User fakeUser = new User();
        fakeUser.setEmail("test@test.com");
        fakeUser.setPasswordHash("hashed_password");
        fakeUser.setUserRole(UserRole.PARTICIPANT);

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtService.generateToken(any())).thenReturn("fake.jwt.token.123");

        String resultToken = authService.login(request);

        assertEquals("fake.jwt.token.123", resultToken);
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void testLogin_InvalidPassword_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@test.com");
        request.setPassword("wrong_password");

        User fakeUser = new User();
        fakeUser.setEmail("test@test.com");
        fakeUser.setPasswordHash("hashed_password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(fakeUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void testLogin_UserNotFound_ThrowsException() {
        LoginRequest request = new LoginRequest();
        request.setEmail("notfound@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authService.login(request));
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void testRegister_UserAlreadyExists_ThrowsException() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("notfound@test.com");
        request.setPassword("password123");

        User fakeUser = new User();
        fakeUser.setEmail("test@test.com");
        fakeUser.setPasswordHash("hashed_password");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(fakeUser));

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void testRegister_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newuser@test.com");
        request.setPassword("password123");
        request.setFullName("Иван Иванов");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("hashed_password");

        String resultMessage = authService.register(request);

        assertEquals("Пользователь зарегестрирован", resultMessage);
        verify(userRepository, times(1)).save(any(User.class));
        verify(userProfileService, times(1)).createProfileForUser(any(User.class));
        verify(passwordEncoder, times(1)).encode("password123");
    }
}