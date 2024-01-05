package com.BZD.Chat_Engine_Demo.Login_System;
import java.util.List;

/**
 * Interface defining the business logic to be implemented for user-related operations.
 */
public interface UserService {
    void saveUser(UserTransfer userDto);

    User findByEmail(String email);

    List<UserTransfer> findAllUsers();

    String updateUsername(String email, String newUsername);

    String updatePassword(String email, String newPassword);

    boolean emailExists(String email);

    boolean usernameExists(String username);
}
