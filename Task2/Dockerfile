#FROM ubuntu:latest
#LABEL authors="neYa"
#
#ENTRYPOINT ["top", "-b"]

FROM openjdk:21
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]