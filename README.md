# Pulmonis API

## Required setup
Java 8

Latest Maven

Latest Docker

Running server is possible only on **Unix-like** operating systems

## Before starting up
Execute `./do.sh migrate` in root directory of the project to insert migrations.

### Required environment variables
        ENV_DATABASE_HOST=
        ENV_DATABASE_PORT=6002
        ENV_DATABASE_NAME=pulmonis
        ENV_DATABASE_USERNAME=unicornGary
        ENV_DATABASE_PASSWORD=magicalRainbow
        ENV_DATABASE_CONNECTION_STRING=
## Starting up

Execute `./do.sh start` in root directory of the project to start API.

For documentation visit `localhost:6001/docs`

## Run using docker

1. Compile image with API
`docker build --build-arg SERVER_PORT=6001 -t api-kotlin .`

2. Run docker compiled docker images
`docker run -it --env ENV_SERVER_PORT=6001 -p 6001:6001 api-kotlin`
