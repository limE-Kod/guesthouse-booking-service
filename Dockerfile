FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]





#FROM eclipse-temurin:17-jre

#WORKDIR /app

#COPY target/*.jar app.jar

#EXPOSE 8080

#ENTRYPOINT ["java", "-jar", "app.jar"]