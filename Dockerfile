# 1. ビルド環境（MavenとJavaが入ったコンテナ）
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
# テストをスキップしてjarファイルを作る
RUN mvn clean package -DskipTests

# 2. 実行環境（Javaだけの軽量コンテナ）
FROM eclipse-temurin:17-jdk
# ビルド環境で作った app.jar をここにコピー
COPY --from=build /app/target/app.jar app.jar
# ポート8080を開ける
EXPOSE 8080
# アプリを起動する
ENTRYPOINT ["java", "-jar", "app.jar"]