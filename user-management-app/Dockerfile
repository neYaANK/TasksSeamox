#FROM ubuntu:latest
#LABEL authors="neYa"
#
#ENTRYPOINT ["top", "-b"]

FROM openjdk:21
ARG JAR_FILE=Task2/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]