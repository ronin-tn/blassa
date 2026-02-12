# ---- Build Stage ----
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:resolve -B

COPY src/ src/
RUN ./mvnw package -Dmaven.test.skip=true -B
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080


ENTRYPOINT ["java", "-jar", "app.jar"]
