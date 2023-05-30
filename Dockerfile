FROM openjdk:11-jdk
MAINTAINER project-solvo
 
WORKDIR /app

EXPOSE 80

COPY server/build/install/server /app
#RUN ./gradlew clean :server:installDist --no-daemon --info
RUN chmod +x "/app/bin/server"

ENTRYPOINT cd "/app" && "./bin/server"
