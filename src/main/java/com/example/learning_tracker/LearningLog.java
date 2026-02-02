package com.example.learning_tracker;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat; // ← これをインポート
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity // 「これはデータベースのテーブルになりますよ」という目印
public class LearningLog {

	@Id // 主キー（Primary Key）
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 自動採番 (Auto Increment)
	private Long id;
	@Column(nullable = false)
	@DateTimeFormat(pattern = "yyyy-MM-dd") // ★この1行を追加！

	private LocalDate date; // 日付
	private String category; // カテゴリ（Java, Spring, 雑記など）
	private String content; // 学習内容
	private int progress; // 進捗度 (0-100%)
	// ▼ これを追加！「この記録の持ち主」
    @ManyToOne
    @JoinColumn(name = "user_id")
    private SiteUser user;

	// 必須：空のコンストラクタ
	public LearningLog() {}

	// 便利なコンストラクタ（テストデータ作成用）
	public LearningLog(LocalDate date, String category, String content,
			int progress) {
		this.date = date;
		this.category = category;
		this.content = content;
		this.progress = progress;
	}

	// Eclipseの機能でGetter/Setterを生成してください
	// （ソースメニュー > GetterおよびSetterの生成、で全フィールドを選択）
	// 面倒なら、ここから下に手書きせずに自動生成してみてください。

	/**
	 * idのGetter
	 * @return id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * idのSetter
	 * @param id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * dateのGetter
	 * @return date
	 */
	public LocalDate getDate() {
		return date;
	}

	/**
	 * dateのSetter
	 * @param date
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}

	/**
	 * categoryのGetter
	 * @return category
	 */
	public String getCategory() {
		return category;
	}

	/**
	 * categoryのSetter
	 * @param category
	 */
	public void setCategory(String category) {
		this.category = category;
	}

	/**
	 * contentのGetter
	 * @return content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * contentのSetter
	 * @param content
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * progressのGetter
	 * @return progress
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * progressのSetter
	 * @param progress
	 */
	public void setProgress(int progress) {
		this.progress = progress;
	}

	// GetterとSetterを追加 (Lombokがあるなら不要)
    public SiteUser getUser() { return user; }
    public void setUser(SiteUser user) { this.user = user; }

}
