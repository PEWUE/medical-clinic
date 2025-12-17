FROM alpine/java:21-jdk
COPY target/medical-clinic-0.0.1-SNAPSHOT.jar medical-clinic-app.jar
ENTRYPOINT ["java", "-jar", "/medical-clinic-app.jar"]