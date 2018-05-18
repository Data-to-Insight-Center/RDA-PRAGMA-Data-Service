
# Rice Genomics Data Identity Services

A minimal RESTful API and service that links PID management (through PIT service and DTR service) to repositories.

Earlier this module used to written by Spring framework and its libraries. As it is mainly use for Restful services we rewritten the code using normal Java REST API for RESTful web services (JAX-RS).

## Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher 
3. MongoDB Server V3.0 or higher

## Building the Source
Check out source codes and move to Data Service Server directory:
```
git clone https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service.git
cd ./data-service-server-rewrite
```
Edit the default.properties file under src/main/resources; set PIT/DTR uri and set middleware web service server address and port
```
vi default.properties
```
Install by maven install:
```
mvn clean install -Dmaven.test.skip=true
```
Deploy Data Service Server WAR under Tomcat container
```
cp ./target/data-service-server-rewrite.war <tomcat>/webapps/
```
## Contributing
This software release is under Apache 2.0 licence.
