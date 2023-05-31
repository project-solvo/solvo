FROM openjdk:17-jdk
MAINTAINER project-solvo

EXPOSE 80

# Build
#COPY . /app
#RUN ./gradlew clean :server:installDist --no-daemon --no-parallel "-Pkotlin.compiler.execution.strategy=in-process" "-Dorg.gradle.jvmargs=-Xmx4096m" "-Dfile.encoding=UTF-8"
#RUN chmod +x "/app/server/build/install/server"
#
#ENTRYPOINT cd "/app/server/build/install/server" && "./bin/server"

# Copy server
#COPY server/build/install/server /app
#RUN chmod +x "/app/bin/server"
#
#ENTRYPOINT cd "/app" && "./bin/server"

# Run server via Gradle
COPY . /app
RUN ./gradlew clean :server:build

ENTRYPOINT "./gradlew :server:run"
