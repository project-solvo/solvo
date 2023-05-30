FROM openjdk:11-jdk
MAINTAINER project-solvo
 
WORKDIR /app

EXPOSE 80

COPY . /app
RUN ./gradlew clean :server:installDist --no-daemon --info --no-parallel
RUN chmod +x "/app/server/build/install/server"

ENTRYPOINT cd "/app/server/build/install/server" && "./bin/server"
