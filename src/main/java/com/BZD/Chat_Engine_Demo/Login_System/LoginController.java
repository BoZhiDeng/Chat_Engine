package com.BZD.Chat_Engine_Demo.Login_System;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Controller for managing login and registration requests.
 */
@Controller
public class LoginController {

    private UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    // Handler for the home page
    @GetMapping("index")
    public String home() {
        return "index";
    }

    // Handler for displaying the login form
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    // handler method to handle user registration request
    @GetMapping("register")
    public String showRegistrationForm(Model model) {
        UserTransfer user = new UserTransfer();
        model.addAttribute("user", user);
        return "register";
    }

    // handler method to handle register user form submit request
    @PostMapping("/register/save")
    public String registration(@Valid @ModelAttribute("user") UserTransfer user,
            BindingResult result,
            Model model) {
        if (userService.emailExists(user.getEmail())) {
            result.rejectValue("email", null, "There is already an account registered with that email");
        }

        if (userService.usernameExists(user.getUsername())) {
            result.rejectValue("username", null, "There is already an account registered with that username");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.saveUser(user);
        return "redirect:/register?success";
    }

    @GetMapping("/users")
    public String listRegisteredUsers(Model model) {
        List<UserTransfer> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    //Handler to updating username of input user by email
    @PostMapping("/updateUsername")
    @ResponseBody
    public ResponseEntity<?> updateUsername(@RequestParam("email") String email,
            @RequestParam("username") String newUsername) {
        String status = userService.updateUsername(email, newUsername);
        switch (status) {
            case "SUCCESS":
                return ResponseEntity.ok().body("Username updated successfully");
            case "USERNAME_ALREADY_EXISTS":
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("There is already an account registered with that username");
            case "USER_NOT_FOUND":
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            default:
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    //Handler to updating password of input user by email
    @PostMapping("/updatePassword")
    @ResponseBody
    public ResponseEntity<?> updatePassword(@RequestParam("email") String email,
            @RequestParam("password") String newPassword) {
        try {
            String status = userService.updatePassword(email, newPassword);
            if ("SUCCESS".equals(status)) {
                return ResponseEntity.ok().body("Password updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

}
