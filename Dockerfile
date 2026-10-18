FROM gradle:9.8.1-jdk21-alpine AS build
WORKDIR /app
# first copy gradle files to optimize caching
# prevents dependencies download when code changes
COPY build.gradle settings.gradle ./ 
COPY gradle/ gradle/
RUN gradle dependencies --no-daemon
COPY src/ src/
# SpringBoot needs bootJar
RUN gradle bootJar --no-daemon

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S app && adduser -S app -G app
COPY --from=build /app/build/libs/app.jar app.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]