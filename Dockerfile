# Etapa 1: Build da aplicação
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar arquivos de configuração
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Dar permissão e baixar dependências
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copiar código fonte
COPY src ./src

# Compilar
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagem final
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copiar JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Porta
EXPOSE 8080

# Comando para rodar
ENTRYPOINT ["java", "-jar", "app.jar"]