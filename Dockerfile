FROM node:20-bookworm AS client-builder
LABEL maintainer="Arturo Volpe <arturovolpe@gmail.com>"

# Install Chromium and dependencies for Puppeteer
RUN apt-get update && apt-get install -y \
    chromium \
    chromium-sandbox \
    --no-install-recommends \
    && rm -rf /var/lib/apt/lists/*

# Set environment variable to use system Chromium
ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true \
    PUPPETEER_EXECUTABLE_PATH=/usr/bin/chromium

ENV BUILD_FOLDER=/home/node/client
RUN mkdir -p "$BUILD_FOLDER" && chown node:node "$BUILD_FOLDER"
WORKDIR $BUILD_FOLDER

# Switch to node user
USER node

COPY --chown=node:node client/. "$BUILD_FOLDER"

RUN npm ci --ignore-scripts
RUN npm run build

FROM eclipse-temurin:17-jdk-jammy AS builder
ARG BRANCH_NAME
WORKDIR /app
COPY mvnw /app
COPY .mvn /app/.mvn
COPY pom.xml /app
COPY src /app/src
COPY utils /app/utils

COPY --from=client-builder /home/node/client/dist /app/src/main/resources/public
RUN sh mvnw clean
RUN sh ./utils/gen_licenses.sh
RUN sh mvnw -B -q package
RUN sh mvnw -B -q clean install -Ppostgres -DskipTests


FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
COPY --from=builder /app/target/cotizaciones-2.1.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

