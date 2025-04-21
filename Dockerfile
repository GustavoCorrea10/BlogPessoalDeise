FROM maven:3.8.5-openjdk-17 AS builder
WORKDIR /app
COPY . . 

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENV PORT=8080
EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
