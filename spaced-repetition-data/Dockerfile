FROM gradle:8.7.0-jdk21-alpine AS build

WORKDIR /workspace

# Копируем файлы Gradle
COPY ./spaced-repetition-data/gradle ./spaced-repetition-data/gradle
COPY ./spaced-repetition-data/gradlew ./spaced-repetition-data/
COPY ./spaced-repetition-data/gradle.properties ./spaced-repetition-data/
COPY ./spaced-repetition-data/build.gradle.kts ./spaced-repetition-data/
COPY ./spaced-repetition-data/settings.gradle.kts ./spaced-repetition-data/

# Делаем gradlew исполняемым
WORKDIR /workspace/spaced-repetition-data
RUN chmod +x gradlew

# Копируем исходный код модуля
COPY ./spaced-repetition-data/src ./src

# Собираем приложение
RUN ./gradlew build -x test --no-daemon

# Финальный образ
FROM openjdk:21-jdk-slim
EXPOSE 8080
COPY --from=build /workspace/spaced-repetition-data/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]