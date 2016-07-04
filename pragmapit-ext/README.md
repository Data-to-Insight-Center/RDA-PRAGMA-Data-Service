# PRAGMA PIT Ext

This extended tool of PIT derives from Research Data Alliance PIT working group. RDA PID Information Types Working Group was established to specify a framework for PID information types (PITs5), to develop consensus on some essential types, and to define a process by which other types can be integrated. The PID WG was formally approved after initial discussions at the first RDA Plenary in Göteborg in March 2013 as one of the first two working groups. The other working group reaching approval at that time was the Data Type Registries (DTR) WG, which also formed a core dependency for the work of the PIT WG, since the core types designed by the PIT WG were planned to be registered in the type registry. The PIT WG was planned to end after 18 months at the 4th RDA Plenary in September 2014 in Amsterdam.

Based on PIT outcomes, we devote efforts to extend PIT service with Profile level support for usage of scientific communities. We also improve compatibility issues with the latest version of Data Type Registry (Cordra V 1.0.7) developed from CNRI.


# Conceptual Model

In this model, every PID record consists of a number of properties. Every property bears a PID and its essential elements are a name, a range and a value. Only the PID and the value are stored in PID records, while the name and range are available from the registered property definition in the type registry. Every property is registered in the type registry, and aside from the property range and name, the type registry record provides additional information such as a description text and provenance information such as author, creation date and a contact address.

A type consists of a number of properties, which are subdivided into mandatory and optional. Every type is registered in the type registry, thus bearing a PID, description text and provenance information. A PID record is said to conform to a type if it provides all mandatory properties of that type. If a PID record is filtered by type, all mandatory and optional properties will be returned.

A profile consists of several types. A PID record conforms to a profile if it provides all mandatory properties of all types of the profile. There are two models for understanding profiles. In the first model, a profile is not necessarily globally discoverable and thus does not bear a global PID. This lessens the barriers of using profiles by individual communities, but such profiles are less shareable. In the second model, a profile is registered in the type registry. This motivates re-use of profiles and may increase interoperability, but the mandatory registration of profiles may make their usage too costly. The prototype implements the first model through a somewhat minimalistic emulation.

![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/pragmapit-ext/docs/tableview.png)

![alt tag](https://raw.githubusercontent.com/Data-to-Insight-Center/RDA-PRAGMA-Data-Service/master/pragmapit-ext/docs/datamodel.png)

# Installation Guide

## Software Dependencies

1. Apache Maven V3.0 or higher
2. JDK V1.6 or higher
3. Cordra V1.0.7
4. Apache Tomcat V7 or higher

## Install Data Type Registry (Latest: Cordra V1.0.7)
Please refer to https://cordra.org/ for software downloading and installation.

Configuration with dataType schema:

```
1) Go to the admin UI: http://<host>:<port>/admin.html
2) Sign in as admin.
3) Click on the Schemas section.
4) Click the “Add Schema” button.
5) In the “Add Schema” dialog they must set the name of the new schema to “dataType” 
6) Select the “Empty” template and click “OK".
7) In the text editor for the schema copy and paste the contents of file docs/dataType.json
8) Click "Save"
```

## Install and Deploy PRAGMA PIT Ext service
1) Build source to generate a web application archive (war) file:

```
mvn clean install -Dmaven.test.skip=true
```

2) PIT ext service must be configured properly so it knows which identifier service and type registry to contact.
An example configuration file in Java's properties file format can be found as testing.properties.example. Copy it to /usr/local/rda/pitapi.properties and make sure the application server's user has sufficient permissions to read it. Also update it with the addresses of the Handle System 8 instance and possibly the Type Registry. The config file contains the same properties that are also used for testing.

3) Deploy the generated war file on your application server (e.g., Tomcat)

```
cp <pragmapit-ext>/target/pragmapit-ext-0.2.war <tomcat>/webapps/
```

4) A simple test to verify that the API is running properly can be made by calling the {@link rdapit.rest.TypingRESTResource#simplePing ping} method:

```
curl http://your.server/your.application.path/pitapi/ping
>Hello World
```

# Contribution
This software is attributed to the original work from RDA PIT working group. Code extension is under ISC license.

# Release History
* 0.2.0 1st Release 2016.07.04



