FROM java:8-jre

ADD ./build/libs/tracker-service-1.0.0.RELEASE.jar app.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-Xmx2048m" ,"-jar", "/app.jar"]

EXPOSE 6000