# Rice Genomics Data Identity Services

A minimal RESTful API and service that links PID management (through PIT service and DTR service) to repositories.

# Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher 
3. MongoDB Server V3.0 or higher

## Building the Source

Check out source codes:
```
git clone https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service.git
```
Install handle.net client package to your local Maven Repository
```
cd ./Data-Service-server
mvn install:install-file -Dfile=./lib/handle-client.jar -DgroupId=Handle.net -DartifactId=handle-client -Dversion=1.0 -Dpackaging=jar
```

Edit the MongoConfig.xml file found under src/main/resources and set your backend mongoDB uri with username/password if exists.
```
vi Data-Service-server/src/main/resources/MongoConfig.xml
```
Edit the SpringConfig.properties file under src/main/resources; set PIT/DTR uri and set middleware web service server address and port
```
vi Data-Service-server/src/main/resources/SpringConfig.properties
```

Build RDA-PRAGMA Data Service 
```
mvn clean install
```
If you want to skip maven test, run the following cmd:
``` 
mvn clean install -Dmaven.test.skip=true
```

## Deploy Data Service server WAR under Tomcat container
```
cp ./target/pragma-dataidentity-server.war <tomcat>/webapps/
```

# Contributing
This software release is under Apache 2.0 licence.
