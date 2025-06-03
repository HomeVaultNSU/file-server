FROM openjdk:21-jdk

WORKDIR /service

COPY deploy/app.jar /service/file-server.jar

EXPOSE 8080

CMD ["java", "-jar", "file-server.jar"]
