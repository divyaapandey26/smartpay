package com.example.smartpay;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtUtil jwtUtil;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String name,
                         @RequestParam String username,
                         @RequestParam String password,
                         @RequestParam(defaultValue = "1000") double balance,
                         Model model) {

        // Check if username already exists
        boolean exists = userRepository.findAll().stream()
                .anyMatch(u -> username.equals(u.getUsername()));

        if (exists) {
            model.addAttribute("error", "Username already taken. Please choose another.");
            return "signup";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters.");
            return "signup";
        }

        User user = new User(name, username,
                passwordEncoder.encode(password), balance, "ROLE_USER");
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}