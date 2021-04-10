FROM hypriot/rpi-java
ADD /target/portfolio-watch-0.0.1-SNAPSHOT.jar /opt/portfolio-watch.jar
CMD  ["java","-Dspring.mail.username=${EMAIL_USER}","-Dspring.mail.password=${EMAIL_PASS}","-Dtd-ameritrade.redirect=${TD_REDIRECT}","-Dtd-ameritrade.client-id=${TD_CLIENT_ID}","-Dfinancial-modeling-prep.api-key=${FMP_API_KEY}","-jar","/opt/portfolio-watch.jar"]
