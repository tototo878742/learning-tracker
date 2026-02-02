package com.example.learning_tracker;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private SiteUserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // SecurityConfigで定義する暗号化マシン

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. DBからユーザーを探す
        SiteUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 2. Spring Securityが理解できる形式(User)に変換して返す
        return new User(user.getUsername(), user.getPassword(),
                Collections.emptyList()); // 権限リストは一旦空でOK
    }

    // 会員登録用のメソッド
    public void registerUser(SiteUser user) {
        // パスワードを暗号化してから保存する！ (必須)
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
    }
 // --- 既存の registerUser メソッドの下あたりに追加 ---

    // ユーザー情報の更新
    public void updateUser(Long id, String newEmail, String newPassword) {
        // IDを使ってDBからユーザーを探す
        SiteUser user = userRepository.findById(id).orElseThrow();

        user.setEmail(newEmail);

        // パスワードが入力されていたら、暗号化して変更する
        if (!newPassword.isEmpty()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }

    // ユーザーの削除（退会）
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}