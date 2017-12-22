# RDA-PRAGMA Data Service Galaxy Tassel5 Workflow Client

RDA-PRAGMA Data Service includes PID assignment, resolution and data typing services using Data Type Registry. 
In this architecture, Data Service client for Galaxy Tasse5 workflow is used to collect both Data Objects and metadata objects generated from Tassel 5 workflow execution. Tassel 5 workflow DOs include genotype/phynotype files, model output and workflow description JSON file. Metadata objects contain DO's creation date, checksum (namely as etag), DigitalObjectLocation (URL), etc.

The features of this client include:

1. Minimum instrumentation - Interact with Tassel5 pipeline without touching Tassel core code base;

2. User transparency - Automatically harvest DOs when workflow is executed from Galaxy Engine;

3. Plug & Play model - With minor updates to clients such framework can be used to harvest DO from applications across domains;

## Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher 
3. Galaxy Workflow Tassel5 Toolset

##Building the Source
Check Out Source Codes:
```
git clone https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service.git
```
Install by Maven Install:
```
cd ./Data-Service-client-galaxy
mvn install -Dmaven.test.skip=true
```
Update clientConfig.properties file for individual Galaxy Workflow instance:
```
vi ClientConfig.properties
```
Move config and Data-service-client-galaxy.tar file into Galaxy Tassel5 toolshed folder.

##Contributing
This software release is under ISC licence.







