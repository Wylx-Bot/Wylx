ARG APP_DIR=/opt/wylx
ARG BUILD_DIR=/tmp/wylx

# Build Container
FROM gradle:jdk17-alpine AS alpine-build

USER gradle:gradle

ARG BUILD_DIR
WORKDIR $BUILD_DIR
COPY --chown=gradle:gradle . $BUILD_DIR
RUN ./gradlew build --no-daemon

RUN unzip $BUILD_DIR/build/distributions/wylx.zip -d $BUILD_DIR/unzip

# Final Application Container
FROM eclipse-temurin:17-jre-alpine
ARG BUILD_DIR
ARG APP_DIR
WORKDIR $APP_DIR

RUN apk add --no-cache libgcc

RUN addgroup -S wylx
RUN adduser -S wylx -G wylx
USER wylx:wylx

COPY --from=alpine-build $BUILD_DIR/unzip $APP_DIR/

ENTRYPOINT [ "./wylx/bin/wylx" ]