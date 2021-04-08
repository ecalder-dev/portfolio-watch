#!/bin/bash
mvn clean install
docker build -t portfolio-watch .
docker run -dp 9200:9200 --name portfolio-watch-container portfolio-watch