Spring Data OrientDB
====================

The primary goal of the [Spring Data](http://projects.spring.io/) project is to make it easier to build Spring-powered applications that use new data access technologies such as non-relational databases, map-reduce frameworks, and cloud based data services.

The SpringData OrientDB project will implement easy to use APIs for using OrientDB as a Document database and as a Graph database. 

The document module is based on the [Spring Data MongoDB](http://projects.spring.io/spring-data-mongodb/) project. 

[![Build Status](https://drone.io/github.com/vidakovic/spring-data-orientdb/status.png)](https://drone.io/github.com/vidakovic/spring-data-orientdb/latest)

To include OrientDB Spring Data in your Java project via Maven, put one of the snippets below in your pom.xml or build.gradle

### Graph API

Maven:
```xml
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>spring-data-orientdb-graph</artifactId>
    <version>0.12</version>
</dependency>
```

Gradle:
```groovy
    group: 'com.orientechnologies', name: 'spring-data-orientdb-graph', version: '0.12'
```


### Document API
```xml
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>spring-data-orientdb-document</artifactId>
    <version>0.12</version>
</dependency>
```

Gradle:
```groovy
    group: 'com.orientechnologies', name: 'spring-data-orientdb-document', version: '0.12'
```

### Object API
```xml
<dependency>
    <groupId>com.orientechnologies</groupId>
    <artifactId>spring-data-orientdb-object</artifactId>
    <version>0.12</version>
</dependency>
```

Gradle:
```groovy
    group: 'com.orientechnologies', name: 'spring-data-orientdb-object', version: '0.12'
```
