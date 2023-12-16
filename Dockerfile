FROM openjdk:17-alpine
MAINTAINER sicyaas.inc
COPY target/VelvetDrive-1.0.0.jar VelvetDrive-1.0.0.jar
ENTRYPOINT ["java","-jar","VelvetDrive-1.0.0.jar"]