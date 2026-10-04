FROM eclipse-temurin:21-jdk-jammy as build
WORKDIR /app
COPY mvnw mvnw
COPY .mvn .mvn
COPY pom.xml pom.xml
RUN mkdir -p src
COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jdk-jammy
ARG JAR_FILE=target/*.jar
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

