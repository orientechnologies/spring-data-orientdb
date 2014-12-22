This sample project is comprehended by the following:
* Spring Boot REST controller
* OrientDB remote object persistence by means of Spring Data
* Shiro session-management with OrientDB-based authorizing realm
* Hazelcast (3.3) powered session distributed persistence

# Pre-requisites

* JDK 8
* Maven 3.2.3 or newer

# Run

```
mvn clean package spring-boot:run
```

# Testing

## REST API (JSON)

### Initialize test scenario

```
curl -i -H "Accept: application/json" -X PUT http://localhost:8080/users
```

### Access protected method without being authenticated

```
curl -i -H "Accept: application/json" -X GET http://localhost:8080/users
```

You should get a ```401 Unauthorized``` response status.

### Log-in

```
curl -i -c cookie.txt -H "Accept: application/json" -H "Content-type: application/json" -X POST -d '{"username":"pires@gmail.com","password":"123qwe"}' http://localhost:8080/users/auth
```

You should get a ```200 OK``` response status and have a valid cookie stored in ```cookie.txt```.

### Access protected method again

```
curl -i -b cookie.txt -H "Accept: application/json" -X GET http://localhost:8080/users
```

You should get a ```200 OK``` response status and some JSON representing existing users.

### Access another protected method to which you don't have permission

```
curl -i -b cookie.txt -H "Accept: application/json" -X GET http://localhost:8080/users/do_something
```

You should get a ```401 Unauthorized``` response status.