package com.example.learning_tracker;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// 必要なインポート
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
// UserDetailsServiceImpl なども必要ならAutowiredする
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LearningLogController {

	@Autowired // これでSpringが自動的にRepositoryの実物を注入してくれます
	private LearningLogRepository repository;
	@Autowired
	private SiteUserRepository userRepository; // ← ユーザー情報を検索するために追加

	@GetMapping("/")
	public String index(
			@AuthenticationPrincipal UserDetails userDetails, // ← ログイン中の人
			@RequestParam(name = "keyword", required = false) String keyword,
			Model model) {

		// ログイン中のユーザーの正体(SiteUser)をDBから特定する
		SiteUser currentUser = userRepository.findByUsername(
				userDetails.getUsername()).orElseThrow();

		List<LearningLog> logs;

		// ★ここが変わる！ findAll() ではなく findByUser(...) を使う
		if (keyword != null && !keyword.isEmpty()) {
			// (検索機能の修正は後回しでもOKですが、やるならここで「自分のデータかつキーワード一致」を探す必要があります)
			// 一旦、検索機能は置いておいて、全件表示だけ直します
//			logs = repository.findByUser(currentUser);
			// ★ここを変更: 新しい検索メソッドを使う
            logs = repository.searchByUserAndKeyword(currentUser, keyword);
		} else {
			// 自分のデータだけ取得
			logs = repository.findByUser(currentUser);
		}

		model.addAttribute("logs", logs);
		model.addAttribute("keyword", keyword);
		return "index";
	}
	// --- ここから追加 ---

	// 1. 登録画面を表示する
	@GetMapping("/register")
	public String showForm(Model model) {
		// 空っぽのデータを作って画面に渡す（ここに入力してもらうため）
		model.addAttribute("learningLog", new LearningLog());
		return "log-form"; // log-form.html を表示する
	}

	// 保存処理
    @PostMapping("/save")
    public String saveLog(
            @AuthenticationPrincipal UserDetails userDetails, // ← ログイン中の人
            @ModelAttribute LearningLog learningLog) {

        // ログイン中のユーザーを特定
        SiteUser currentUser = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();

        // ★データに「持ち主」をセットしてから保存！
        learningLog.setUser(currentUser);

        repository.save(learningLog);
        return "redirect:/";
    }

	// --- ここまで ---
	// 3. 編集画面を表示する
	// URL例: /edit/1 (IDが1番のデータを編集)
	@GetMapping("/edit/{id}")
	public String showEditForm(
			@org.springframework.web.bind.annotation.PathVariable Long id,
			Model model) {
		// IDを元にDBから検索。見つからなければ null になる
		LearningLog log = repository.findById(id).orElse(null);

		// 見つけたデータを画面に渡す（これでフォームに最初から値が入った状態になる）
		model.addAttribute("learningLog", log);
		return "log-form";
	}

	// 4. データを削除する
	// URL例: /delete/1 (IDが1番のデータを削除)
	@GetMapping("/delete/{id}")
	public String deleteLog(
			@org.springframework.web.bind.annotation.PathVariable Long id) {
		// 指定されたIDのデータを削除
		repository.deleteById(id);

		// 削除したら一覧に戻る
		return "redirect:/";
	}
}
