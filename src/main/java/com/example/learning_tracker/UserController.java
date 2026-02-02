package com.example.learning_tracker;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
//必要なインポートを追加してください
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder; // ← これを追加

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private SiteUserRepository userRepository;

    @Autowired
    private UserDetailsServiceImpl userService;

    // プロフィール画面の表示
    @GetMapping("/profile")
    public String showProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // ログイン中のユーザー名から、DBの詳しい情報を取得
        SiteUser user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
        model.addAttribute("user", user);
        return "auth/profile";
    }

    // 更新処理
    @PostMapping("/update")
    public String updateUser(@AuthenticationPrincipal UserDetails userDetails,
                             @RequestParam String email,
                             @RequestParam(required = false) String password) {

        SiteUser user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Serviceを使って更新
        userService.updateUser(user.getId(), email, password);

        return "redirect:/"; // 更新したらトップへ
    }

    // 退会処理
    @GetMapping("/delete")
    public String deleteUser(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        SiteUser user = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // Serviceを使って削除
        userService.deleteUser(user.getId());

     // 2. 強制ログアウト処理 (書き換え)
        // セッションを無効化する
        request.getSession().invalidate();
        // セキュリティの文脈(Context)を空っぽにする
        SecurityContextHolder.clearContext();

        // 3. すっきりした状態でトップ（またはログイン画面）へ
        return "redirect:/";
    }
}