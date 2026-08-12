FROM maven:3-eclipse-temurin-26 AS build

WORKDIR /build

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY src ./src

RUN mvn clean package -DskipTests

RUN ls -lh target

FROM eclipse-temurin:26
WORKDIR /app

COPY --from=build /build/target/auto-search-service*.jar auto-search-service.jar

EXPOSE 8100

CMD ["java", "-jar", "auto-search-service.jar"]
