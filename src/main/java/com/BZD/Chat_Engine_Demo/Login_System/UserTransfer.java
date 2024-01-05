package com.BZD.Chat_Engine_Demo.Login_System;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for User entity to handle user information in a safe way.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTransfer
{
    private Long id;
    @NotEmpty
    private String username;
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;
    @NotEmpty(message = "Password should not be empty")
    private String password;
}
