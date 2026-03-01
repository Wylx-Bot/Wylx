ARG APP_DIR=/opt/wylx
ARG BUILD_DIR=/tmp/wylx

# Build Container
FROM gradle:jdk25 AS alpine-build

USER gradle:gradle

ARG BUILD_DIR
WORKDIR $BUILD_DIR
COPY --chown=gradle:gradle . $BUILD_DIR
RUN ./gradlew build --no-daemon

RUN unzip $BUILD_DIR/build/distributions/wylx.zip -d $BUILD_DIR/unzip

# Final Application Container
FROM eclipse-temurin:25-jre
ARG BUILD_DIR
ARG APP_DIR
WORKDIR $APP_DIR

RUN addgroup --system wylx
RUN adduser --system wylx
RUN usermod -a -G wylx wylx
USER wylx:wylx

COPY --from=alpine-build $BUILD_DIR/unzip $APP_DIR/

ENTRYPOINT [ "./wylx/bin/wylx" ]