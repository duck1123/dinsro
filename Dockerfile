# FROM clojure
FROM openjdk:8-alpine

LABEL author="Duck Nebuchadnezzar <duck@kronkltd.net>"

RUN mkdir -p /dinsro/data
ADD target/uberjar/dinsro.jar /dinsro/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/dinsro/app.jar"]
