# Autonomous Vehicle Data Processing
[![Build Status](https://travis-ci.com/fuvidani/autonomous-vehicle-data-processing.svg?token=nWakM5wh7rnyXAfUiELD&branch=master)](https://travis-ci.com/fuvidani/autonomous-vehicle-data-processing) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
 
Data processing of autonomous vehicles in a fault-tolerant, resilient and asynchronous microservice environment. Featuring Kotlin, Spring Boot, Spring 5, MongoDB, RabbitMQ, Docker, Kubernetes.

# Development

### Start Mongo (Docker)
In the terminal, navigate to `/autonomous-vehicle-data-processing/database` (where the Dockerfile resides).
Then:

`docker build .`

And run:

`docker run -p 27017:27017 <IMAGE_ID>` 

Additionally, you can test the connection to the different databases through
the mongo shell:
- `mongo YOUR_LOCAL_IP:27017/vehicleDatabase -u 'vehicleService' -p 'vehicleDatabasePassword'`
- `mongo YOUR_LOCAL_IP:27017/trackerDatabase -u 'trackerService' -p 'trackerDatabasePassword'`
- `mongo YOUR_LOCAL_IP:27017/statisticsDatabase -u 'statisticsService' -p 'statisticsDatabasePassword'`
- `mongo YOUR_LOCAL_IP:27017/notificationDatabase -u 'notificationService' -p 'notificationDatabasePassword'`

### Start RabbitMQ (Docker)
`docker run -p 5672:5672 --hostname localhost rabbitmq:3` (change `localhost` accordingly)


