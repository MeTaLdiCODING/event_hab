package com.event.hab.auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @Size(min =6, max = 30, message = "Пароль от 6 до 30 символов")
    @NotBlank(message = "Пароль обязателен")
    private String password;
}
