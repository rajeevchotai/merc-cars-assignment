# Connected Cars Platform Developer
This project consists of 3 modules written in Java 11 and Spring Boot Framework
- **common**: Contains files that are shared by the other services. This module contains
  - **AESEncryptionManager.java**: AES-256 encryption using Galois/Counter Mode (GCM) for various data types- byte [], String, Integer and Double
  - **ProtobufJmsMessageConverter**: Convert JMS messages to and from to Google’s protocol buffer format 
- **frontend**: Spring Boot microservice exposing a REST API frontend with 3 endpoints
    1.	**Store** – to save new data
    2.	**Update** – to update existing data
    3.	**Read** – to read existing data, Read need not contain the fileType header or parameter
    - This microservice accepts data in JSON format, encrypts the data and cnverting to the Google’s protocol buffer format. 
    - The store and update data is then send to a JMS ActiveMQ queue which can be read from the backend microservice
    - To Read data it calls a REST API from the backend to get the data which is decrypted and shown to user in JSON Format
    - JUnit has been written for this
- **backend**: Spring Boot microservice exposing a REST API frontend for the *read* endpoint. 
  - Service listens to the JMS ActiveMQ queue for messages, receives and decrypts the information from the JMS message.
  -	Once decrypted the service stores the information either in CSV/XML file based on the input received in the fileType header in the JMS message.
  -	It also stores the data in a H2 database to allow easier CRUD operations.
  -	If the data should be read and returned then the returned data is encrypted.


## Build and Deploy
All the projects use Maven 3 as the build tool. Maven Wrapper files (mvw) are provided as part of the repo that can be used to run the Maven project without having Maven installed and present on the path. Java 11 however needs to be installed beforehand.

First build the common module using
`./mvnw clean install` on Linux or `mvnw clean install` on Windows

The other two modules can be built independently using
`./mvnw clean package spring-boot:repackage` on Linux or `mvnw clean package spring-boot:repackage` on Windows

## Running the application
The frontend can be run using `java -jar frontend-1.0.0.jar` present in the *target* directory inside the frontend module
The frontend can be run using `java -jar backend-1.0.0.jar` present in the *target* directory inside the backend module

## Access the application
Access can be done via the Swagger UI 
- **frontend**: http://localhost:8080/frontend/documentation/swagger-ui/
- **backend**: http://localhost:8082/backend/documentation/swagger-ui/



 
