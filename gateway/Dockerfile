FROM java:8-jre

ADD ./build/libs/gateway-0.1.0.jar app.jar
CMD ["java", "-Dspring.profiles.active=prod", "-Djava.security.egd=file:/dev/./urandom", "-Xmx2048m", "-jar", "/app.jar"]

EXPOSE 4000