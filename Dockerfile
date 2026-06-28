# Build
FROM maven:3-eclipse-temurin-26-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Production
FROM eclipse-temurin:25-alpine
WORKDIR /app
COPY --from=build /app/target/pagefolk-*.jar app.jar
EXPOSE 8080
RUN addgroup -S spring && adduser -S spring -G spring
USER spring
ENTRYPOINT ["java", "-jar", "app.jar"]