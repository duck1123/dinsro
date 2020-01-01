FROM clojure
# FROM openjdk:8-alpine

LABEL maintainer="Duck Nebuchadnezzar <duck@kronkltd.net>"
MAINTAINER Duck Nebuchadnezzar <duck@kronkltd.net>

WORKDIR /usr/src/app

COPY . /usr/src/app
ADD target/uberjar/dinsro.jar /dinsro/app.jar

EXPOSE 3000
# EXPOSE 8080

RUN lein deps

# 6
CMD ["java", "-jar", "/dinsro/app.jar"]
# CMD ["script/server"]
