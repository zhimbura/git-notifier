FROM gradle:8.9.0-jdk22 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /app
COPY . .
RUN gradle buildFatJar

FROM amazoncorretto:22-jdk
WORKDIR /app
COPY --from=build /app/build ./app

EXPOSE 8080:8080
ENTRYPOINT ["java","-jar","app/libs/app.jar"]