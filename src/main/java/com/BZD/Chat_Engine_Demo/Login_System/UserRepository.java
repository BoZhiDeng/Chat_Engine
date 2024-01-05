package com.BZD.Chat_Engine_Demo.Login_System;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for User entity, providing an abstraction to manage user entities.
 */
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
