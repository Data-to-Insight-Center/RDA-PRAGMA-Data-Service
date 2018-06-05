# Data Identity GUI

A frontend layer. UI design and JS scripts which send AJAX call to middle layer web services and display response in users' browser.

# Installation Guide

## Software Dependencies

1. JavaScript V1.8.0 or higher
2. Python V2.6 or higher

## Building the Source
Check out source codes:
```
git clone https://github.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service.git
```
## Deploy Data Identity client frontend layer UI
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

# Contributing
This software release is under Apache 2.0 licence.
