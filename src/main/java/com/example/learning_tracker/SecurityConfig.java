package com.example.learning_tracker;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
//アカウントをデータベースで管理する場合のクラス
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/css/**", "/js/**", "/h2-console/**").permitAll()
                .requestMatchers("/auth/**").permitAll() // 登録画面などはログインなしでOK
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/auth/login") // 自作のログイン画面を使う設定
                .defaultSuccessUrl("/", true) // ログイン成功時の飛び先
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        // H2コンソール用設定
        http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // パスワードをハッシュ化する強力な計算機
        return new BCryptPasswordEncoder();
    }
}