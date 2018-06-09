# Autonomous Vehicle Data Processing
[![Build Status](https://travis-ci.com/fuvidani/autonomous-vehicle-data-processing.svg?token=nWakM5wh7rnyXAfUiELD&branch=master)](https://travis-ci.com/fuvidani/autonomous-vehicle-data-processing) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
 
Data processing of autonomous vehicles in a fault-tolerant, resilient and asynchronous microservice environment. Featuring Kotlin, Spring Boot, Spring 5, MongoDB, RabbitMQ, Docker, Kubernetes.

# Useful shortcuts

* data-processor	9000
* data-simulator	10000
* gateway		4000
* notification	7000
* statistic	8000
* tracker		6000
* vehicle		5000
* rabbit-mq	15672 | 5672 
* frontend 	8069
* eu.gcr.io/dse-group-05/***
* pushing to google cloud registry: gcloud docker -- push eu.gcr.io/dse-group-05/***


# Development

### Start Mongo (Docker)
In the terminal, navigate to `/autonomous-vehicle-data-processing/database` (where the Dockerfile resides).
Then:

`docker build .`

And run:

`docker run -p 27017:27017 <IMAGE_ID>` 

Additionally, you can test the connection to the different databases through
the mongo shell:
- `mongo YOUR_LOCAL_IP:27017/vehicleDatabase -u "vehicleService" -p "vehicleDatabasePassword"`
- `mongo YOUR_LOCAL_IP:27017/trackerDatabase -u "trackerService" -p "trackerDatabasePassword"`
- `mongo YOUR_LOCAL_IP:27017/statisticsDatabase -u "statisticsService" -p "statisticsDatabasePassword"`
- `mongo YOUR_LOCAL_IP:27017/notificationDatabase -u "notificationService" -p "notificationDatabasePassword"`

### Start RabbitMQ (Docker)
`docker run -p 5672:5672 -p 15672:15672 --hostname localhost rabbitmq:3-management` (change `localhost` accordingly)


### Some useful JSONs
Sample `VehicleDataRecord`:
```json
{"id":null,"timestamp":1526126033768,"metaData":{"identificationNumber":"JH4DB8590SS001561","model":"1995 Acura Integra"},"sensorInformation":{"location":{"lat":0.0,"lon":0.0},"proximityInformation":{"distanceToVehicleFrontInCm":0.0,"distanceToVehicleBehindInCm":0.0},"passengers":4,"speed":50.0},"eventInformation":"NONE"}
```


