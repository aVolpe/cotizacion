FROM node:carbon as client-builder
WORKDIR /client
COPY client/package.json /client/
COPY client/package-lock.json /client/
RUN npm install
COPY client/. /client/
RUN ls -la /client
RUN npm run build

FROM maven:3.5.3-alpine as builder
WORKDIR /app
COPY pom.xml /app
COPY src /app/src
COPY --from=client-builder /client/dist /app/src/main/resources/public
RUN ls -l 
RUN mvn package -DskipTests

FROM openjdk:8-jdk-alpine
VOLUME /tmp
COPY --from=builder /app/target/cotizaciones-1.0.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

