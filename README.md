# PRAGMA Data Identity Service

Scientific workflows produce data that contribute to published results. In an effort to make the data products more shareable, we piloted persistent ID (PID) services as part of the Pacific Rim Applications and Grid Middleware (PRAGMA) project. The services work with VMs created through PRAGMA cloud VM (http://www.pragma-grid.net/), and export data objects and their provenance from the VMs at conclusion of execution. These data objects are assigned a persistent ID using the Handle system.   

The pilot data service leverages two recommendations from the Research Data Alliance (RDA, https://rd-alliance.org/): Persistent Identifier Information Type (PIT) and Data Type Registry (DTR). The objective of the project is to enhance sharing of data objects specifically from genomic analyses by the International Rice Research Institute (http://irri.org/) community. 

PRAGMA Data Identity is a three-layer architecture:

1. Back end storage repository: a persistent MongoDB noSQL store fronted by a simple RESTful API that serves as repository for application specific metadata and file archive for data objects. 
2. PRAGMA Data Identity Service: a minimal RESTful API and service that links PID management (through PIT service and DTR service) to repositories.     
3. Frontend Layer:  UI design and JS scripts which send AJAX call to middle layer web services and display response in users' browser.

Three interaction diagrams below illustrate the interactions amongst the layers for three specific scenarios (or actors).  They are read top to bottom, left to right:

##### Scenario/Actor 1.
A repository management framework, which has actions to construct and register minimal metadata record, domain metadata record, create landing page, and ingest digital objects.

![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/docs/DOUpload.png)
                              Fig. 1. Data object upload sequence through DataIdentity Service

##### Scenario/Actor 2.
A repository management framework, which has actions to respond to an external request for a data object. 
![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/docs/DORetrieval.png) 
                              Fig. 2. Data object retrieval sequence through DataIdentity Service  

##### Scenario/Actor 3.
A middleware service, with no user interaction, which takes a large list of PIDs that have minimal metadata and does downselecting in a completely programmatic way.
![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/docs/MiddlewareService.png) 
                              Fig. 3. Retrieval of minimal PID metadata through the DataIdentity Service by programmatic access 

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. Python V2.6 or higher 
4. MongoDB Server V3.0 or higher
5. JavaScript V1.8.0 or higher
6. PRAGMAPIT-Ext and Cordra V1.0.7: Refer to https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/tree/master/pragmapit-ext

## Hardware Requirements

1. This software can be deployed on physical resources or VM instance with public network interface.
2. For public access, it requires 3 ports (backend repo, web service APIs, UIs) which iptables rules allow traffic through the firewall.

## Contributing
This software is created in part through funding from the National Science Foundation under award #1234983, and from an adoption award through Research Data Alliance/US.  This software is licensed under an Apache 2.0 license.

## Release History
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
