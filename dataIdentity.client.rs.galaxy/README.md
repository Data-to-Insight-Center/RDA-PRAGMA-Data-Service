# Data Identity Service Client for Galaxy Tassel5 Workflow

RDA-PRAGMA Data Identity Service includes PID assignment, resolution and data typing services using Data Type Registry. 
In this architecture, Data Identity Service Client for Galaxy Tasse5 workflow is used to collect both Data Objects and metadata objects generated from Tassel 5 workflow execution. Tassel 5 workflow DOs include genotype/phynotype files, model output and workflow description JSON file. Metadata objects contain DO's creation date, checksum (namely as etag), DigitalObjectLocation (URL), etc.

Earlier this module used to written by Spring framework and its libraries. As it is mainly use for Restful services we rewritten the code using normal Java REST API for RESTful web services (JAX-RS).

The features of this client include:

1. Minimum instrumentation - Interact with Tassel5 pipeline without touching Tassel core code base;

2. User transparency - Automatically harvest DOs when workflow is executed from Galaxy Engine;

3. Plug & Play model - With minor updates to clients such RESTful web framework can be used to harvest DO from applications across domains;

# Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher 
3. Galaxy Workflow Tassel5 Toolset

## Building the Source
Check out source code and move to data identity client directory:
```
git clone https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service.git
cd ./dataIdentity.client.rs.galaxy
```
Install by Maven Install:
```
mvn install -Dmaven.test.skip=true
```
Update clientConfig.properties file for individual Galaxy Workflow instance:
```
vi ClientConfig.properties
```
Move config and dataIdentity.client.rs.galaxy.jar file into Galaxy Tassel5 toolshed folder.

## Configure Galaxy Tassel5
Follow this link to setup the Galaxy. 
https://galaxyproject.org/admin/get-galaxy/

By default, it is configured into 8080 port. Few things need to be considered during the Galaxy setup, that are listed below.
Before run the “run.sh” file make sure you did the following.
* Copy the below files into this directory path (/root/galaxy/shed_tools/toolshed.g2.bx.psu.edu/repos/dereeper/tassel5/652aafd88060/tassel5)
  1. admpriv.bin
  2. ClientConfig.properties
  3. dataIdentity.client.rs.galaxy.jar (after you build the “DataIdentity-Client-Galaxy” it will produce this jar file into the target folder)

* Update the configuration for the below files
  1. tassel.sh
  2. tassel.xml

# Contributing
This software release is under Apache 2.0 licence.
