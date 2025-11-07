FROM maven:3.8.6-openjdk-11-slim AS build

WORKDIR /app

# Copia o pom.xml e baixa as dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e compila
COPY src ./src
RUN mvn clean package -DskipTests

# Imagem final (mais leve)
FROM openjdk:11-jre-slim

WORKDIR /app

# Copia o JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]