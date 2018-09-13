# Data Identity GUI

It is a website frontend layer for IRRI data products. UI design(HTML and CSS) and JS scripts which send AJAX call to middle layer web services and display response in users' browser. 

## Installation Guide

## Software Dependencies

1. JavaScript V1.8.0 or higher
2. Apache Tomcat V7.0 or higher

## Building the Source
Check out source code and move to DataIdentity-GUI directory:
```
git clone https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service.git
cd ./DataIdentity-GUI
```
## Deploy Data Identity client frontend layer UI
Configure frontend and middleware connections
```
vi DataIdentity-GUI/javascript/config.js
```

Deploy DataIdentity-GUI directory under Tomcat container
```
cp ./DataIdentity-GUI <tomcat>/webapps/
```

Start the server and website should be accessible through the following URL.
```
http://host:port/DataIdentity-GUI/irri-index.html
```

# Contributing
This software release is under Apache 2.0 licence.

