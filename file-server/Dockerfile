FROM openjdk:21-jdk

WORKDIR /service

COPY deploy/app.jar /service/app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
