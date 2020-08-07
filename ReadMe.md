# Market-Clients app
> App allows management of clients, purchases and products collections read from JSON file. This is Maven multi-module console project with a built-in menu.

## Table of contents
* [Technologies](#technologies)
* [Setup](#setup)
* [Author](#Author)

## Technologies
#### General:
* Java 14
* [Apache Maven](https://maven.apache.org/) 3.6.3 

#### Dependencies:
* [Gson](https://github.com/google/gson/gson) 2.8.6
* [Lombok](https://projectlombok.org/) 1.18.12

#### Plugins:
* [Maven AntRun Plugin](http://maven.apache.org/plugins/maven-antrun-plugin/) 1.3
* [Maven Assembly Plugin](https://maven.apache.org/plugins/maven-assembly-plugin/) 3.2.0
* [Maven Clean Plugin](https://maven.apache.org/plugins/maven-clean-plugin/) 2.5
* [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/) 3.8.1
* [Maven Dependency Plugin](https://maven.apache.org/plugins/maven-dependency-plugin/) 2.8
* [Maven Deploy Plugin](https://maven.apache.org/plugins/maven-deploy-plugin/) 2.7
* [Maven Enforcer Plugin](https://maven.apache.org/enforcer/maven-enforcer-plugin/) 3.0.0-M3
* [Maven Flatten Plugin](https://www.mojohaus.org/flatten-maven-plugin/) 1.2.1
* [Maven Install Plugin](https://maven.apache.org/plugins/maven-install-plugin/) 2.4
* [Maven JAR Plugin](https://maven.apache.org/plugins/maven-jar-plugin/) 2.4
* [Maven Release Plugin](https://maven.apache.org/maven-release/maven-release-plugin/) 2.5.3
* [Maven Resources Plugin](https://maven.apache.org/plugins/maven-resources-plugin/) 3.1.0
* [Maven Site Plugin](https://maven.apache.org/plugins/maven-site-plugin/) 3.9.0
* [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/) 2.12.4

## Setup
To build application from root directory of project run command

`mvn clean install`

Then copy resources folder and app.jar file from target folder in your ui module. Paste it anywhere. Then you can run app from console with the command

`java -jar --enable-preview app.jar`

## Author
Jan Wiśniewski