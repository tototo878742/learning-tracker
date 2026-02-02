package com.example.learning_tracker;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SiteUserRepository extends JpaRepository<SiteUser, Long> {
    // ユーザー名で検索する機能を追加
    Optional<SiteUser> findByUsername(String username);
}