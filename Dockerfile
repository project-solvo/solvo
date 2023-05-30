FROM openjdk:11-jdk
MAINTAINER project-solvo
 
WORKDIR /app

EXPOSE 80

COPY . .
RUN ./gradlew clean :server:installDist --no-daemon --info
RUN chmod +x "/app/server/build/install/server/bin/server"

ENTRYPOINT cd "/app/server/build/install/server" && "./bin/server"