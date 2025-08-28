# ---- Stage 1: Build & Test ----
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy only necessary files to optimize layer caching
COPY pom.xml mvnw ./
COPY .mvn .mvn

COPY src src

# If tests pass, proceed with packaging
RUN chmod +x ./mvnw
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw clean package

# ---- Stage 2: Create the final image ----
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Copy only the built JAR
COPY --from=build /app/target/*.jar bff.jar

EXPOSE 7081

ENTRYPOINT ["java", "-jar", "bff.jar"]
