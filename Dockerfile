FROM ubuntu:latest AS BUILD
RUN apt-get update && apt-get install -y openjdk-17-jdk maven
COPY . .
RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim
EXPOSE 8080
COPY --from=BUILD /target/todolist-1.0.0mvn.jar app.jar
ENTRYPOINT [ "java", "-jar", "app.jar" ]