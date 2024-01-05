package com.BZD.Chat_Engine_Demo.Login_System;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class implementing UserService interface to manage user data.
 */
@Service
public class UserServiceHelper implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceHelper(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserTransfer userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserTransfer> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public String updateUsername(String email, String newUsername) {
        if (userRepository.existsByUsername(newUsername)) {
            return "USERNAME_ALREADY_EXISTS";
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return "USER_NOT_FOUND";
        }
        user.setUsername(newUsername);
        userRepository.save(user);
        return "SUCCESS";
    }

    @Override
    public String updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return "SUCCESS";
        } else {
            return "USER_NOT_FOUND";
        }
    }

    @Override
    public boolean emailExists(String email) {
        if(this.findByEmail(email) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    private UserTransfer convertEntityToDto(User user) {
        UserTransfer userDto = new UserTransfer();
        String[] name = user.getName().split(" ");
        userDto.setUsername(user.getUsername());
        userDto.setFirstName(name[0]);
        userDto.setLastName(name[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }
}
