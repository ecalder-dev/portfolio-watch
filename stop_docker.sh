#!/bin/bash
mvn clean install
docker stop portfolio-watch-container
docker rm portfolio-watch-container