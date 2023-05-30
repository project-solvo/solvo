FROM openjdk:11-jdk
MAINTAINER project-solvo
 
WORKDIR /app

EXPOSE 80

COPY . /app
RUN ./gradlew clean :server:installDist --no-daemon --no-parallel "-Pkotlin.compiler.execution.strategy=in-process" "-Dorg.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8"
RUN chmod +x "/app/server/build/install/server"

ENTRYPOINT cd "/app/server/build/install/server" && "./bin/server"
