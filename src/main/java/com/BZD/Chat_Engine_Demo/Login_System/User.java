package com.BZD.Chat_Engine_Demo.Login_System;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class for User, mapped to 'users' table in the database.
 * Contains user identification and authentication information.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    // Primary Key with auto-increment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Unique username, cannot be null
    @Column(nullable = false, unique = true)
    private String username;

    // User's full name, cannot be null
    @Column(nullable = false)
    private String name;

    // Unique email address, cannot be null
    @Column(nullable = false, unique = true)
    private String email;

    // Encrypted password, cannot be null
    @Column(nullable = false)
    private String password;
}
