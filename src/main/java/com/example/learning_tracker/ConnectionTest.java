package com.example.learning_tracker;

import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionTest {
	public static void main(String[] args) {
		// application.properties に書いた内容をそのままここにコピーしてください
		String url
				= "jdbc:postgresql://ep-xxxx.ap-southeast-1.aws.neon.tech/neondb?sslmode=require";
		// ユーザー名を新しく作った "teacher" に変更
		String user = "teacher";

		// パスワードをSQLで設定した "Portfolio2026" に変更
		String password = "Portfolio2026";

		System.out.println("接続テスト開始...");
		try {
			Connection conn = DriverManager.getConnection(url, user, password);
			System.out.println("成功！データベースに接続できました！");
			conn.close();
		} catch (Exception e) {
			System.out.println("失敗... 原因はこちら↓");
			e.printStackTrace();
		}
	}
}
