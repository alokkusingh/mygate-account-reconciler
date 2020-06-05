FROM openjdk:8-jdk-alpine
MAINTAINER Alok Singh (alok.ku.singh@gmail.com)
VOLUME /tmp
COPY target/account-reconciler-0.0.1-SNAPSHOT.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/urandom","-jar","/app.jar"]