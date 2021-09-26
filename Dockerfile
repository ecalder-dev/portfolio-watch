FROM openjdk:8-jdk-alpine
ADD /target/portfolio-watch-0.0.1-SNAPSHOT.jar /opt/portfolio-watch.jar
CMD  ["java", "-Dspring.datasource.url=${DB_URL}","-Dspring.mail.username=${EMAIL_USER}","-Dspring.mail.password=${EMAIL_PASS}","-jar","/opt/portfolio-watch.jar"]
