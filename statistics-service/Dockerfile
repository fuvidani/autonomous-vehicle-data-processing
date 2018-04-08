FROM java:8-jre

ADD ./build/libs/statistics-service-0.1.0.jar app.jar
CMD ["java","-Dspring.data.mongodb.host=dse-mongo-db" , "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar"]

EXPOSE 8000