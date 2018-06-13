FROM java:8-jre

ADD ./build/libs/vehicle-service-0.1.0.jar app.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xmx2048m", "-jar", "/app.jar"]

EXPOSE 5000