# Estágio de build
FROM ubuntu:latest AS build
RUN apt-get update && apt-get install -y openjdk-17-jdk maven
COPY . .
RUN mvn clean install -DskipTests

# Estágio de execução (usando imagem estável e leve)
FROM eclipse-temurin:17-jre-alpine
EXPOSE 8080
COPY --from=build /target/todolist-1.0.0mvn.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]