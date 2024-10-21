package com.CollabSpace.authService.dtos;

import com.CollabSpace.authService.enums.Interests;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
public class UserDto {

    private String userId;

    @NotBlank(message = "Please Enter your Full Name")
    private String name;

    @Email(message = "Enter a valid email!!")
    private String email;


    @Size(min=4,max = 8,message = "Password must be min 4 characters to maximum 8 characters")
    private String password;

    private String profileImage;

    @Enumerated
    private Interests interests;
}
