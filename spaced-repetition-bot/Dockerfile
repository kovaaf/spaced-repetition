FROM maven:3.9.11-amazoncorretto-17-alpine AS build

# Минимальная информация о git для плагинов
COPY .git/HEAD /workspace/.git/HEAD
COPY .git/refs /workspace/.git/refs

WORKDIR /workspace

# Копируем корневой pom.xml
COPY pom.xml .

WORKDIR /workspace/spaced-repetition-bot

# Копируем pom.xml модуля
COPY /spaced-repetition-bot/pom.xml /workspace/spaced-repetition-bot
# Скачиваем зависимости для модуля
RUN mvn -B dependency:go-offline

# Копируем исходный код модуля
COPY /spaced-repetition-bot/src /workspace/spaced-repetition-bot/src

# Собираем приложение
RUN mvn -B package -DskipTests -Dcheckstyle.skip

# Финальный образ
FROM openjdk:17-jdk-slim
LABEL maintainer="ouidockeruser"
EXPOSE 8080
COPY --from=build /workspace/spaced-repetition-bot/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]