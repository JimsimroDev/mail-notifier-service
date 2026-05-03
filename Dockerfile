FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR workspace
COPY pom.xml .
COPY src ./src
#Compila y genera el jar
RUN mvn clean package -DskipTests
# Extraemos las capas usando layertools
RUN java -Djarmode=layertools -jar target/*.jar extract


# Etapa 2: Imagen de ejecución
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
# Copia las capas desde el builder
COPY --from=builder workspace/dependencies/ ./
COPY --from=builder workspace/spring-boot-loader/ ./
COPY --from=builder workspace/snapshot-dependencies/ ./
COPY --from=builder workspace/application/ ./

# Usamos el JarLauncher para un arranque óptimo
ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher", "--spring.config.location=classpath:/application-docker.yml"]
