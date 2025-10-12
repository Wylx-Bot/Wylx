ARG APP_DIR=/opt/wylx
ARG BUILD_DIR=/tmp/wylx

# Build Container
FROM gradle:jdk17-alpine AS alpine-build

RUN apk update
RUN apk add git

USER gradle:gradle

ARG BUILD_DIR
WORKDIR $BUILD_DIR
COPY --chown=gradle:gradle build.gradle settings.gradle $BUILD_DIR/
COPY --chown=gradle:gradle src $BUILD_DIR/src
COPY --chown=gradle:gradle gradle $BUILD_DIR/gradle
COPY --chown=gradle:gradle .git $BUILD_DIR/.git
RUN ./gradlew build --no-daemon

RUN unzip $BUILD_DIR/build/distributions/WylxBot.zip -d $BUILD_DIR/unzip

# Final Application Container
FROM eclipse-temurin:17-jre-alpine
ARG BUILD_DIR
ARG APP_DIR
WORKDIR $APP_DIR

RUN addgroup -S wylx
RUN adduser -S wylx -G wylx
USER wylx:wylx

COPY --from=alpine-build $BUILD_DIR/unzip $APP_DIR/

ENTRYPOINT [ "./WylxBot/bin/WylxBot" ]