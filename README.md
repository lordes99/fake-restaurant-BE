# FakeRestaurant

A Spring Boot 3.4.5 application built with Java 23 and Gradle.
It's the backend of a university project that allows users to create
restaurants and write reviews. [Here Frontend](https://github.com/lordes99/fake-restaurant-FE).
This project integrates OpenAPI Generator, Flyway, JPA, MapStruct, MinIO, JWT, and other key
components to provide a modern, modular backend architecture.

## Technologies
This project is using: 
- [OpenAPI](https://swagger.io/specification/) version 7.11.0;
- [Flyway](https://github.com/flyway/flyway);
- [MapStruct](https://mapstruct.org/) version 1.5.5.Final;
- [MinIO](https://www.min.io/) version 8.5.17;
- [Docker](https://www.docker.com/);
- [PostgresSQL](https://www.postgresql.org/) version 17-alpine;

## Development server
Before starting a local development server, you have to run a build process that includes openApi generator, using: 
```bash
./gradlew clean build
```
You have to add properties on `application.properties` file, in directory named `secrets`.

With the Makefile you can run docker compose with the services necessary for the application, using:
```bash
make up
```

Now you can run the application. Once the server is running on [http://localhost:8080/](http://localhost:8080/).

After you stop the application, you can stop docker compose with the command: 
```bash
make down
```
