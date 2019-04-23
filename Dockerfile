FROM openjdk:8-jdk-alpine

VOLUME /tmp

EXPOSE 8080

COPY bcbe-0.0.1-SNAPSHOT.jar bcbe.jar

#Run the jar
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/.urandom", "-jar", "bcbe.jar"]