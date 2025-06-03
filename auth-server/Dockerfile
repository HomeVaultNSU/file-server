FROM openjdk:21-jdk

WORKDIR /service

COPY deploy/app.jar /service/auth-server.jar

EXPOSE 8090

CMD ["java", "-jar", "auth-server.jar"]
