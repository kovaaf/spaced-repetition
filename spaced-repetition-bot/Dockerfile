FROM maven:3.9.11-amazoncorretto-17-alpine AS build

# Минимальная информация о git для плагинов
COPY .git/HEAD /workspace/.git/HEAD
COPY .git/refs /workspace/.git/refs

WORKDIR /workspace

# Копируем pom.xml
COPY ./pom.xml .
COPY ./spaced-repetition-bot/pom.xml ./spaced-repetition-bot/pom.xml

# Скачиваем зависимости для модуля
RUN mvn -B -f ./spaced-repetition-bot/pom.xml dependency:go-offline

# Копируем исходный код модуля
COPY ./spaced-repetition-bot/src ./spaced-repetition-bot/src

# Собираем приложение
RUN mvn -B -f ./spaced-repetition-bot/pom.xml  package -DskipTests -Dcheckstyle.skip

# Финальный образ
FROM openjdk:17-jdk-slim
LABEL maintainer="ouidockeruser"
EXPOSE 8080
COPY --from=build /workspace/spaced-repetition-bot/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]