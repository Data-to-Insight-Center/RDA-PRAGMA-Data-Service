# RDA-PRAGMA Data Service Galaxy Tassel5 Workflow Client

RDA-PRAGMA Data Service includes PID assignment, resolution and data typing services using Data Type Registry. 
In this architecture, Data Service client for Galaxy Tasse5 workflow is used to collect both Data Objects and metadata objects generated from Tassel 5 workflow execution. Tassel 5 workflow DOs include genotype/phynotype files, model output and workflow description JSON file. Metadata objects contain DO's creation date, checksum (namely as etag), DigitalObjectLocation (URL), etc.

#Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher 
3. Galaxy Workflow Tassel5 Toolset

##Building the Source
Check Out Source Codes:
```
git clone https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service.git
```
Install handle.net client package to your local Maven Repository
```
cd ./Data-Service-client-galaxy
mvn install -Dmaven.test.skip=true
```

##Contributing
This software release is under ISC licence.







