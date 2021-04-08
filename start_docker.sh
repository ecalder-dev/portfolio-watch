#!/bin/bash
docker build -t portfolio-watch .
docker run -dp 9200:9200 --name portfolio-watch-container \
--env EMAIL_USER=$EMAIL_USER --env EMAIL_PASS=$EMAIL_PASS \
--env KEY_STORE_LOC=$KEY_STORE_LOC --env KEY_STORE_LOC=$KEY_STORE_LOC \
--env KEY_STORE_PASS=$KEY_STORE_PASS --env TD_REDIRECT=$TD_REDIRECT \
--env TD_CLIENT_ID=$TD_CLIENT_ID --env FMP_API_KEY=$FMP_API_KEY \
portfolio-watch