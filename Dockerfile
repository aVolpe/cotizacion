FROM alekzonder/puppeteer:1.3.0 as client-builder
MAINTAINER Arturo Volpe <arturovolpe@gmail.com>
RUN whoami
ENV BUILD_FOLDER /home/pptruser/client
RUN mkdir "$BUILD_FOLDER"
WORKDIR $BUILD_FOLDER

COPY client/package.json "$BUILD_FOLDER"
COPY client/package-lock.json "$BUILD_FOLDER"

RUN npm install
COPY --chown=pptruser:pptruser client/. "$BUILD_FOLDER"
RUN npm run build

FROM adoptopenjdk:15-hotspot as builder
ARG BRANCH_NAME
WORKDIR /app
COPY mvnw /app
COPY .mvn /app/.mvn
COPY pom.xml /app
COPY src /app/src
COPY utils /app/utils

COPY --from=client-builder /home/pptruser/client/dist /app/src/main/resources/public
RUN ./mvnw clean
RUN sh ./utils/gen_licenses.sh
RUN sh mvnw -B -q package
RUN sh mvnw -B -q install -Ppostgres -DskipTests


FROM adoptopenjdk:15-hotspot
VOLUME /tmp
COPY --from=builder /app/target/cotizaciones-2.0.1.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

