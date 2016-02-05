# RDA-PRAGMA-Data-Service

RDA-PRAGMA-Data-Service is designed for users to quick access biodiversity data objects metadata and lineage. It also provides data comparison service to detect if output has been updated due to possible mutations. It is composed of a three-layer architecture:

1. Backend layer: Backend layer includes one persistent MongoDB service which is served as metadata repository and file archive which hosts biodiversity objects.
2. Middle Layer: Middle layer includes web service using Spring framework which responds to queries or ingestions by accessing backend mongoDB and file archive.
3. Frontend Layer: Frontend layer includes UI design and JS scripts which send AJAX call to middle layer web services.

Here is a general architecture graph for Lifemapper Landing page:
![alt tag](https://raw.githubusercontent.com/Gabriel-Zhou/RDA-PRAGMA-Data-Service/master/docs/architecture.png)

Some sample landing pages are as below:

* Projection Set 

hdl.handle.net/11723/1f9e6fa2-6d62-434b-97f0-8f5cc0c16160

* Occurrence Set 

hdl.handle.net/11723/23f927b5-f72f-42cd-a464-788e9941fa39

#Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. Python V2.6 or higher with SimpleHttpServer module
4. MongoDB Server V3.0 or higher
5. JavaScript V1.8.0 or higher

##Building the Source
Check Out Source Codes:
```
git clone https://github.com/Gabriel-Zhou/RDA-PRAGMA-Data-Service.git
```
Edit the SpringConfig.properties file found under src/main/resources and set your backend mongoDB uri with username/password if exists.
```
vi Data-Service-server/src/main/resources/SpringConfig.properties
```
Build Komadu Pingback Model
```
mvn clean install
```

##Deploy Data Service server using nohup
```
nohup java -jar ./target/Data-Service-server-0.1.1-SNAPSHOT.jar &
```

##Deploy Data Service client frontend layer UI
Configure http server port number -- "PORT"
```
vi Data-Service-client/SimpleServer.py

```
Run python SimpleHttpServer with POST enabled
```
python SimpleServer.py
```

##Contributing
This software release is under ISC licence.

##Release History
* 0.1.1 3rd release
* 0.1.0 2nd release
* 0.0.1 Initial release 








