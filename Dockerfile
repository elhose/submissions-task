FROM maven:3.8.1-jdk-11-slim AS build
COPY src ./src
COPY pom.xml .
RUN mvn clean package -DskipTests

FROM openjdk:11.0.10-jre-slim
COPY --from=build ./target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]