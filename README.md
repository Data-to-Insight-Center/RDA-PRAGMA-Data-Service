# PRAGMA-RDA DataIdentity Service

PRAGMA-RDA Data Service brings persistent IDs and registration of data objects generated by scientific analysis that is carried out using a PRAGMA cloud VM (http://www.pragma-grid.net/). The data service leverages two recent recommendations from the Research Data Alliance (RDA, https://rd-alliance.org/): Persistent Identifier Information Type (PIT) and Data Type Registry (DTR). The objective of the project is to enhance sharing of data objects specifically from genomic analyses by the International Rice Research Institute (http://irri.org/) community. This service is designed to be as reusable as possible beyond this for other cases where VMs are used for analysis and PIDs are used to enhance sharability and reusability of results.  

PRAGMA-RDA DataIdentity is a three-layer architecture:

1. Backend storage repository: a persistent MongoDB noSQL store fronted by a simple RESTful API that serves as repository for application specific metadata and file archive for data objects. 
2. PRAGMA-RDA Data Identity Service: a minimal RESTful API and service that links PID management (through PIT service and DTR service) to repositories.     
3. Frontend Layer:  UI design and JS scripts which send AJAX call to middle layer web services and display response in users' browser.

Three interaction diagrams below illustrate the interactions amongst the layers for three specific scenarios (or actors).  They are read top to bottom, left to right:

#####Scenario/Actor 1.
A repository management framework, which has actions to construct and register minimal metadata record, domain metadata record, create landing page, and ingest digital objects.

![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/docs/DOUpload.png)
                              Fig. 1. Data object upload sequence through DataIdentity Service

#####Scenario/Actor 2.
A repository management framework, which has actions to respond to an external request for a data object. 
![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/docs/DORetrieval.png) 
                              Fig. 2. Data object retrieval sequence through DataIdentity Service  

#####Scenario/Actor 3.
A middleware service, with no user interaction, which takes a large list of PIDs that have minimal metadata and does downselecting in a completely programmatic way.
![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/docs/MiddlewareService.png) 
                              Fig. 3. Retrieval of minimal PID metadata through the DataIdentity Service by programmatic access 
                              


#Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. Python V2.6 or higher 
4. MongoDB Server V3.0 or higher
5. JavaScript V1.8.0 or higher

## Hardware Requirements

1. This software can be deployed on physical resources or VM instance with public network interface.
2. For public access, it requires 3 ports (backend repo, web service APIs, UIs) which iptables rules allow traffic through the firewall.

##Building the Source
Check Out Source Codes:
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

##Deploy Data Service server WAR under Tomcat container
```
cp ./target/pragma-dataidentity-server.war <tomcat>/webapps/
```

##Deploy Data Service client frontend layer UI
Configure http server port number -- "PORT"
```
vi Data-Service-client/SimpleServer.py

```

Configure frontend and middleware connections
```
vi Data-Service-client/javascript/config.js

```

Run python SimpleHttpServer with POST enabled
```
nohup python SimpleServer.py &
```

##Contributing
This software release is under ISC licence.

##Release History
* 0.2.5 7th release 07.05.2016
  Release note:
    
    1/ Separate Data Identity server API with PRAGMA repository service API;

    2/ First interaction with PIT service (PRAGMAPIT-ext) instead of DTR service;
    
    3/ Support community(profile) level datatype definition query 
    
* 0.2.0 6th release

  Release note:
    
    1/ APIs to store DO initial upload to staging Database;

    2/ APIs to transfer DO to permanent repository when DO get registered with PID;
    
    3/ CRUD APIs for DOs in staging database (user can upload DO, Read DO, Update DO by data and metadata, delete DO);
    
    4/ Update landing page GUI to present domain metadata and pretty-print json format on web browser end 
    
    5/ Junit test coverage for APIs

* 0.1.5 5th release
* 0.1.2 4th release
* 0.1.1 3rd release
* 0.1.0 2nd release
* 0.0.1 Initial release 








