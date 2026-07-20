# Étape 1 — Build
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

# Étape 2 — Run
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/module-api/target/module-api-*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]