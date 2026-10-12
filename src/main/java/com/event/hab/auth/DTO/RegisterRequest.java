package com.event.hab.auth.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Email обязателен")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Пароль обязателен")
    @Size(min =6, max = 30, message = "Пароль от 6 до 30 символов")
    private String password;

    @NotBlank(message = "Имя пользователя обязательно")
    @Size(min = 2, max = 100, message = "Имя от 2 до 100 символов")
    private String fullName;
}
