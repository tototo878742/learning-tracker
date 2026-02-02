package com.example.learning_tracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserDetailsServiceImpl userService;

    // ログイン画面
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // 登録画面
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new SiteUser());
        return "auth/register";
    }

    // 登録処理
    @PostMapping("/register")
    public String register(@ModelAttribute SiteUser user) {
        userService.registerUser(user);
        return "redirect:/auth/login"; // 登録したらログイン画面へ
    }
}