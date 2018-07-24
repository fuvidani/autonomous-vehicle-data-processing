# Autonomous Vehicle Data Processing
[![Build Status](https://travis-ci.com/fuvidani/autonomous-vehicle-data-processing.svg?token=nWakM5wh7rnyXAfUiELD&branch=master)](https://travis-ci.com/fuvidani/autonomous-vehicle-data-processing) [![ktlint](https://img.shields.io/badge/code%20style-%E2%9D%A4-FF4081.svg)](https://ktlint.github.io/)  [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
 
Data processing of autonomous vehicles in a fault-tolerant, resilient and asynchronous microservice environment. Featuring Kotlin, Spring Boot, Spring 5, MongoDB, RabbitMQ, Docker, Kubernetes.

## Table of content

- [Introduction](#introduction)
    - [About the project](#about-the-project)
    - [Aspects not considered](#aspects-not-considered)
- [Components](#components)
    - [Client](#client)
    - [Vehicles](#vehicles)
    - [Gateway](#gateway)
    - [Notification-Service](#notification-service)
    - [Statistics-Service](#statistics-service)
    - [Tracker-Service](#tracker-service)
    - [Vehicle-Service](#vehicle-service)
    - [Data-Processor](#data-processor)
    - [Persistence](#persistence)
    - [Message Broker](#message-broker)
    - [Data-Simulator](#data-simulator)
- [Installation](#installation)
    - [Local Deployment](#local-deployment)
    - [Google Cloud Deployment](#google-cloud-deployment)
- [API Documentation](#api-documentation)
- [Contributors](#contributors)   
- [License](#license)

## Introduction
This project has been developed during the course "Distributed Systems Engineering" in summer term 2018 at the Vienna University of Technology. There may be decisions or choice of frameworks/libraries that you probably do not agree with - please be advised that some of those were direct requirements of the lecturer that had to be fulfilled. 

### About the project
With the release and authorization of self driving cars of level three, it becomes a challenge to process all the produced data as well as to prepare it in such a way, that information is easily available to stakeholders.

The aim of this project is to develop a software system which is capable of displaying information about self driving cars. Several different stakeholders should have easy access to their respective information they are allowed to use. For example a car manufacturer should be able to query/see information about cars he produced, another stakeholder might be the authorities responsible for security on the roads. These authorities should get informed about (near) crash events to be then able to initiate counter measures. Of course such data is also of interest for other self driving vehicles.

### Aspects not considered
What this project does not try to solve is the complex logic of the self driving cars. This means, that only a simple imitation of the behaviour of the cars is available. Recent years show that security is of major importance in this field. Negative press with hacked cars and drivers out of control have been an issue. Due to the fact that this is a proof-of-concept and not a productive environment, security questions are out of scope. This means, that no authorization, in form of a login for the web or for the backend services, have been implemented.

## Components
The backend’s architecture is composed of a set of independent microservices which are all put behind a gateway acting as a proxy. In this section each component will be described in detail. The diagram below shows the topology of the components.

### Client
The client component is the consumer of the services and is realized through a web application written in React. The application provides an easy-to-use interface for all concerning actors, that is the **manufacturer**, the **traffic authority** and the **emergency service**. The application communicates with the backend through its REST-API exposed by the Gateway component.

### Vehicles
Vehicles have a crucial part in the system. They have a special role, since they are both service consumers and producers. A vehicle connects to the backend through the REST-API and receives up-to-date notifications via WebFlux stream. At the same time it periodically emits a packet of data containing key information about its state and surroundings. This emission can be seen as producer role and is done through the message broker in a fire-and-forget manner.

### Gateway
The gateway encapsulates the microservices at exposes their endpoints to external services as if they belonged to one single service. Its main responsibility is to route incoming traffic to the correct services, preferably with supported load balancing. In case that a service can not be reached, a default hystrix fallback is executed. The gateway also aggregates the API documentation of each microservice to one single, navigable documentation.

### Notification-Service
The notification-service receives multiple types of notifications from the message broker asynchronously and has the responsibility to persist them in its store and to stream them down to the respective consumers. For example, in case of a crash event, the service receives a vehicle notification containing the information which vehicles should receive it and its detailed variant. The service then persists the information and streams the notification down to the correct vehicles.

### Statistics-Service
This service has the purpose of asynchronously receiving reports of past accidents, storing them and making them available through its endpoint. Whenever a new accident report is published, the message broker delivers it to this service who persists it in its data store. If there is already a client connected to this service through a stream, it will be automatically notified about the report without any delay.

### Tracker-Service
The tracker-service is one of the most important service in the infrastructure, because through it manufacturers can track their autonomous vehicles in real-time. In order to support high throughput it is designed to be lightweight: it receives the data packets from vehicles through the message brokes, persists them in the data store and immediately streams them down to the manufacturers. Manufacturers are usually connected to the service via an **infinite stream**. On each incoming data record, the service determines which manufacturer client connections can receive the tracking data and which not.

### Vehicle-Service
This service serves the simple purpose of managing all the vehicles and manufacturers that are available in the system. This is the only service that doesn’t consume any messages from the message broker. It offers the functionality to query all the vehicles, the vehicles of a manufacturer and to register new vehicles.

### Data-Processor
The data-processor is conceptually different from the other services. This component doesn’t offer any external/internal API and no other service can invoke it directly. It solely represents the computing engine which processes each incoming vehicle data record and publishes important events so that the microservices are notified. The data-processor keeps track of the last known location for each vehicle in order to perform geospatial queries when only certain vehicles need to be notified about an accident. It also persists live accidents, i.e. accidents that are currently undergoing and have not yet been cleared by the emergency services. With the help of the message broker, it receives the data records and also publishes messages that are relevant for the microservices. Main functions:
* dispatches notification for manufacturers in case of near-crash event
* dispatches notification for emergency services, manufacturers and vehicles in case of
a crash-event (= accident)
* dispatches notification for vehicles in case the emergency service declares its arrival
at the scene of accident
* dispatches notification for vehicles in case the emergency service claims the accident
to be cleared; dispatches an accident report about the accident

All these dispatched messages are broadcasted via the message broker and consumed by the services that are interested in it.

### Persistence
For persisting all the data previously described, one single MongoDB instance is used. Although the diagram implies that the components all write to the same database, in reality each of them has its own separated one within the instance. For example the notification-service has a connection to the _notificationDatabase_, the tracker-service to the _trackerDatabase_ and the data-processor to the _dataProcessorDatabase_.

### Message Broker
The message broker, specifically RabbitMQ is used to for both delivering data records from vehicles to system services and for delivering messages between the backend’s components. This communication pattern makes the services more independent from each other. Vehicles don’t have to wait for confirmation or perform costly synchronous calls. They simply place their data records on the bus which will be eventually delivered.

### Data-Simulator
The data-simulator is the supportive component for the proof-of-concept and for the use case test. It uses a predefined route from Vienna to Graz to simulate vehicles and possible accidents. The component also offers a REST API so that near-crash, crash events and emergency service events (arrive, cleared) can be manually simulated. It is also possible to restart the current simulation.

## Installation

### Local Deployment
The following list describes the required steps in order to get the system up and running on a local machine using Docker (i.e. Docker and Docker-Compose installations are assumed):
* Execute gradle’s “clean” task to remove old artifacts
* Execute gradle’s “bootJar” task so that the JARs of the microservices get generated
* Open a terminal in the project folder and execute “docker-compose up”. This will
automatically build and deploy the docker images

### Google Cloud Deployment
To get the complete project to the Google Cloud Platform the following steps have to be performed:
* Execute gradle’s “clean” task to remove old artifacts
* Execute gradle’s “bootJar” task so that the JARs of the microservices get generated
* docker build and tag of the images
* docker push to Google Cloud
* Kubernetes cluster deployment

## API Documentation
The available REST-APIs in the backend are all documented with the [Spring REST Docs](https://docs.spring.io/spring-restdocs/docs/current/reference/html5/) library paired with Asciidoctor. These types of API documentations have the advantage of being up-to-date with the current implementation.

## Contributors

Thanks goes to these wonderful people:

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore -->
| [<img src="https://avatars3.githubusercontent.com/u/16260193?s=400&v=4" width="100px;"/><br /><sub><b>David Molnar</b></sub>](https://github.com/dmolnar99)<br />[🤔](#ideas "Ideas and Planning") [💻](https://github.com/fuvidani/web-app-engineering/commits?author=dmolnar99 "Code") [⚠️](https://github.com/fuvidani/web-app-engineering/commits?author=dmolnar99 "Tests") [🎨](#design-dmolnar99 "Design") [📖](#documentation "Documentation") | [<img src="https://avatars3.githubusercontent.com/u/11620189?s=400&v=4" width="100px;"/><br /><sub><b>Lukas Kathrein</b></sub>](https://github.com/lucniner)<br />[🤔](#ideas "Ideas and Planning") [💻](https://github.com/fuvidani/web-app-engineering/commits?author=lucniner "Code") [⚠️](https://github.com/fuvidani/web-app-engineering/commits?author=lucniner "Tests") [📖](#documentation "Documentation") [📦](#packaging "Packaging/Deployment on Kubernetes")|
| :---: | :---: |
<!-- ALL-CONTRIBUTORS-LIST:END -->

## License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).
