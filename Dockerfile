FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR workspace
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jdk-alpine
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/application/ ./
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher", "--spring.config.location=classpath:/application-docker.yml"]
