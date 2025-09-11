# Dockerfile
FROM eclipse-temurin:21-jre-alpine

# Pasta da app
WORKDIR /app

# Copia o jar gerado pelo Maven/Gradle
# Ex.: target/consultoria-springboot-0.0.1-SNAPSHOT.jar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Porta padrão do Spring
EXPOSE 8081

# Para subir com perfil prod
ENV SPRING_PROFILES_ACTIVE=prod

# JVM flags recomendados p/ Cloud Run (memória limitada)
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","/app/app.jar"]
