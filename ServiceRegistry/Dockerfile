FROM eclipse-temurin:25-jdk AS jrebuilder

COPY ./target/ServiceRegistry-0.0.1-SNAPSHOT.jar /app/demo-app.jar

RUN apt-get update && apt-get install -y unzip binutils \
    && unzip /app/demo-app.jar -d temp \
    && $JAVA_HOME/bin/jdeps \
        --print-module-deps \
        --ignore-missing-deps \
        --recursive \
        --multi-release 25 \
        --class-path "./temp/BOOT-INF/lib/*" \
        --module-path "./temp/BOOT-INF/lib/*" \
        /app/demo-app.jar > /jre-modules.txt \
    && $JAVA_HOME/bin/jlink \
        --verbose \
        --add-modules "$(cat /jre-modules.txt)" \
        --strip-debug \
        --no-man-pages \
        --no-header-files \
        --compress=2 \
        --output /jre

FROM debian:bullseye-slim

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=jrebuilder /jre $JAVA_HOME

ARG APPLICATION_USER=appuser
RUN useradd -m -u 1000 $APPLICATION_USER

RUN mkdir /app && chown -R $APPLICATION_USER /app

USER 1000
COPY --chown=1000:1000 ./target/ServiceRegistry-0.0.1-SNAPSHOT.jar /app/demo-app.jar

WORKDIR /app
EXPOSE 8888

ENTRYPOINT ["java", "-jar", "demo-app.jar"]
