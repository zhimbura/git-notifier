FROM gradle:8.12.1-jdk23-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /app
COPY . .
RUN gradle buildFatJar

FROM amazoncorretto:23-jdk
WORKDIR /app
COPY --from=build /app/build ./app

EXPOSE 8080:8080
ENTRYPOINT ["java","-jar","app/libs/app.jar"]