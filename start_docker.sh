#!/bin/bash
docker build -t portfolio-watch .
docker run -dp 9200:9200 --network="host" --name portfolio-watch-container \
--env DB_URL=$DB_URL --env TD_ACCT_ID=$TD_ACCT_ID \
--env EMAIL_USER=$EMAIL_USER --env EMAIL_PASS=$EMAIL_PASS \
--env KEY_STORE_LOC=$KEY_STORE_LOC --env KEY_STORE_LOC=$KEY_STORE_LOC \
--env KEY_STORE_PASS=$KEY_STORE_PASS --env TD_REDIRECT=$TD_REDIRECT \
--env TD_CLIENT_ID=$TD_CLIENT_ID --env FMP_API_KEY=$FMP_API_KEY \
-e "TZ=America/New_York" portfolio-watch
