FROM openjdk:11-jdk
MAINTAINER project-solvo

EXPOSE 80

# Build
#COPY . /app
#RUN ./gradlew clean :server:installDist --no-daemon --no-parallel "-Pkotlin.compiler.execution.strategy=in-process" "-Dorg.gradle.jvmargs=-Xmx4096m" "-Dfile.encoding=UTF-8"
#RUN chmod +x "/app/server/build/install/server"
#
#ENTRYPOINT cd "/app/server/build/install/server" && "./bin/server"

# Copy server
COPY server/build/install/server /app
COPY server/test-sandbox/test-resources /app/test-resources
RUN chmod +x "/app/bin/server"

ENTRYPOINT cd "/app" && export JAVA_OPTS="-Xmx256m" && "./bin/server"

# Run server via Gradle
#COPY . /app
#
#WORKDIR /app
#ENTRYPOINT "./gradlew :server:run"
