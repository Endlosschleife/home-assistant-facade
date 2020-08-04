# Home Assistant API Facade

This project provides a restful API facade for Home Assistant for the [Inkplate Dashboard](https://github.com/Endlosschleife/inkplate-dashboard).

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```
Make sure to set the required environment variables on your shell.
```
export $(cat example.env | xargs)
```

You can access the facade API dashboard under http://localhost:8080/dashboard

## Build
This project provides a Dockerfile for a jvm build only.