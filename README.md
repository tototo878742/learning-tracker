# 日報管理システム (Spring Boot版)

Spring Boot を使用して構築された、Webベースの日報管理アプリケーションです。
従来のServlet/JSP（レガシー構成）との比較学習用に、モダンなアーキテクチャを採用して実装されています。
レガシー版はこちら(※今後公開予定)

Demo URL:https://learning-tracker-tototo878742.onrender.com/


## 概要
ユーザーが日々の業務内容を記録・管理するためのシステムです。
Spring Securityによる認証機能、Spring Data JPAによる効率的なDB操作を備えています。

## 機能一覧

* **ユーザー認証**
  * ログイン / ログアウト
  * 新規会員登録
  * アクセス制御（未ログインユーザーの制限）
* **日報管理 (CRUD)**
  * 新規登録
  * 一覧表示（ログインユーザー自身のデータのみ）
  * 編集
  * 削除

## 技術スタック

* **言語:** Java 17
* **フレームワーク:** Spring Boot 4.0.2
* **テンプレートエンジン:** Thymeleaf
* **データベース:** postgreSQL Neon(本番想定) / H2 (開発用)
* **O/Rマッパー:** Spring Data JPA
* **CSS:** Bootstrap 5

## アーキテクチャ構成

Spring Boot推奨のレイヤードアーキテクチャを採用しています。

1. **Controller (@Controller):** 画面遷移と入力を制御
2. **Service (@Service):** 業務ロジック、トランザクション管理
3. **Repository (@Repository):** データベース操作 (Spring Data JPA)
4. **Entity (@Entity):** データベースのテーブル定義


## 📐 設計図 (Architecture)

### 画面遷移図 
```mermaid
graph LR
    %% --- スタイル定義 (ハイコントラスト設定) ---
    %% 文字色(color)と枠線(stroke)を明示的に指定して、背景色に埋没しないように修正

    %% 通常ノード: 白背景、黒文字、太い枠
    classDef default fill:#ffffff,stroke:#000000,stroke-width:2px,color:#000000;
    
    %% 認可エリアのノード: 濃いめの青、白文字
    classDef protectedNode fill:#1565c0,stroke:#0d47a1,stroke-width:2px,color:#ffffff;
    
    %% エラー: 濃い赤、白文字
    classDef error fill:#c62828,stroke:#b71c1c,stroke-width:2px,color:#ffffff;

    %% 認可エリアの枠線スタイル
    classDef subgraphStyle fill:#363c47,stroke:#1565c0,stroke-width:2px,color:#000000;


    %% --- ノード定義 ---
    Login(Login<br>ログイン画面)
    Register(Register<br>会員登録画面)
    Error(Error<br>エラー画面):::error

    %% --- 認可エリア ---
    subgraph Protected ["認可エリア (要ログイン)"]
        direction TB
        List(List<br>日報一覧画面):::protectedNode
        Create(Create<br>新規登録画面):::protectedNode
        Edit(Edit<br>編集画面):::protectedNode
    end
    
    %% Subgraph自体のスタイル適用（環境によっては効かない場合もありますが、可能な限り調整）
    class Protected subgraphStyle

    %% --- メインフロー (左→右) ---
    Login -->|認証成功| List
    Login -->|新規登録| Register
    Register -->|登録完了| Login

    %% --- 認可エリア内の操作 ---
    List -->|新規作成| Create
    List -->|編集| Edit
    List -->|ログアウト| Login

    %% --- 戻りフロー ---
    Create -->|保存| List
    Create -.->|戻る| List
    Edit -->|更新| List
    Edit -.->|戻る| List

    %% --- 例外フロー ---
    List -.->|例外| Error
    Create -.->|例外| Error
    Edit -.->|例外| Error
```

### クラス図 (Class Diagram)
```mermaid
classDiagram
    %% --- プレゼンテーション層 ---
    class ReportController {
        +index(Model): String
        +create(ReportForm): String
        +edit(id, Model): String
        +delete(id): String
        <<@Controller>>
    }
    class AuthController {
        +login(): String
        +register(UserForm): String
        <<@Controller>>
    }
    class GlobalExceptionHandler {
        +handleException(): String
        <<@ControllerAdvice>>
    }

    %% --- ビジネスロジック層 ---
    class ReportService {
        +findAllByUser(User): List
        +save(Report, User): void
        +delete(id): void
        <<@Service>>
    }
    class CustomUserDetailsService {
        +loadUserByUsername(): UserDetails
        <<@Service>>
    }

    %% --- データアクセス層 (Interface) ---
    class ReportRepository {
        +findByUserOrderByDateDesc(User)
        <<@Repository>>
    }
    class UserRepository {
        +findByUsername(String)
        <<@Repository>>
    }

    %% --- ドメイン層 (Entity) ---
    class User {
        -Long id
        -String username
        -String password
        -List~Report~ reports
        <<@Entity>>
    }
    class Report {
        -Long id
        -LocalDate date
        -String content
        -User user
        <<@Entity>>
    }

    %% --- 関係性 ---
    ReportController --> ReportService : 利用
    AuthController --> CustomUserDetailsService : 利用
    
    ReportService --> ReportRepository : 利用
    CustomUserDetailsService --> UserRepository : 利用
    
    ReportRepository ..> Report : 操作
    UserRepository ..> User : 操作
    
    User "1" *-- "many" Report : 所有 (@OneToMany)
```

### シーケンス図 (Sequence Diagram)
```mermaid
sequenceDiagram
    autonumber
    actor User as ユーザー
    participant Ctrl as ReportController
    participant Svc as ReportService
    participant Repo as ReportRepository
    participant DB as データベース

    Note over User, Ctrl: 画面での操作

    User->>Ctrl: 1. 登録ボタン押下 (POST /reports/create)
    
    activate Ctrl
    Note right of Ctrl: @Validatedで入力チェック<br>(エラーなら画面に戻す)
    
    Ctrl->>Svc: 2. save(reportForm, loginUser)
    activate Svc
    
    Note right of Svc: フォームをEntityに変換<br>ログインユーザーをセット
    
    Svc->>Repo: 3. save(entity)
    activate Repo
    Repo->>DB: 4. INSERT INTO reports...
    DB-->>Repo: 完了
    Repo-->>Svc: 保存されたEntity
    deactivate Repo
    
    Svc-->>Ctrl: void
    deactivate Svc

    Ctrl-->>User: 5. リダイレクト (redirect:/reports)
    deactivate Ctrl
    
    Note over User: 一覧画面へ自動遷移
```

