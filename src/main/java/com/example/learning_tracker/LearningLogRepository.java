package com.example.learning_tracker;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface LearningLogRepository extends JpaRepository<LearningLog, Long> {
    // 中身は空っぽでOK！
    // JpaRepository を継承するだけで、findAll() や save() が勝手に使えるようになります。
	// ▼ この1行を追加！
    // 「カテゴリ」または「内容」に、指定されたキーワードが含まれているか検索する
    List<LearningLog> findByCategoryContainingOrContentContaining(String category, String content);

 // ▼ これを追加！「指定されたユーザーの記録だけを探す」
    List<LearningLog> findByUser(SiteUser user);

 // ▼ これを追加！
    // 意味: 「持ち主(user)が一致」 かつ 「(カテゴリに文字を含む OR 内容に文字を含む)」 データを探せ
    @Query("SELECT l FROM LearningLog l WHERE l.user = :user AND (l.category LIKE %:keyword% OR l.content LIKE %:keyword%)")
    List<LearningLog> searchByUserAndKeyword(@Param("user") SiteUser user, @Param("keyword") String keyword);
}