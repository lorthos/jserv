# Jserv

A Java Http Server

## Building

You will need jdk8, maven and docker

    make package
    
will run tests and package the app as jar.

To build the docker container:

    make docker

## Running inside Docker

    make run-docker
    
## Running on Host Machine

    make run-local-jar
    
## Testing

Run unit and integration tests:

    mvn test

Run manual tests:

    make test1
    make loadtest1
    make test-all

Manual tests might require additional tools installed.
