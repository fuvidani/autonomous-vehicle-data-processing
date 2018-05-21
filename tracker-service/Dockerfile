FROM java:8-jre

ADD ./build/libs/tracker-service-0.1.0.jar app.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

EXPOSE 6000