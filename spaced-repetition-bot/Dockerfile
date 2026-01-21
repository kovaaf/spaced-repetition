FROM maven:3.9.11-amazoncorretto-17-alpine AS build

WORKDIR /workspace

# Копируем pom.xml
COPY ./pom.xml .
COPY ./spaced-repetition-bot/pom.xml ./spaced-repetition-bot/pom.xml

# Скачиваем зависимости для модуля с кэшированием
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -f ./spaced-repetition-bot/pom.xml dependency:go-offline

# Копируем исходный код модуля
COPY ./spaced-repetition-bot/src ./spaced-repetition-bot/src

# Собираем приложение
# Собираем приложение с кэшированием
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -f ./spaced-repetition-bot/pom.xml package \
        -DskipTests \
        -Dcheckstyle.skip

# Финальный образ
FROM amazoncorretto:17-alpine

# Устанавливаем curl для healthcheck
RUN apk add --no-cache curl

EXPOSE 8080
COPY --from=build /workspace/spaced-repetition-bot/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]