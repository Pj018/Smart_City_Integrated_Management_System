package com.example.controller;

import com.example.entity.Role;
import com.example.entity.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model) {
        try {
            if (user.getRole() == null || user.getRole() == Role.ADMIN) {
                user.setRole(Role.CITIZEN); // Default safely
            }
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: Email may already exist.");
            return "register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot_password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        java.util.Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isPresent()) {
            userService.resetPassword(userOptional.get());
            model.addAttribute("successMsg", "Password reset to 'reset123'. Please login.");
        } else {
            model.addAttribute("error", "Email not found.");
        }
        return "forgot_password";
    }
}
