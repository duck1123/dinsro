# FROM clojure
FROM openjdk:8-alpine

LABEL author="Duck Nebuchadnezzar <duck@kronkltd.net>"

VOLUME /var/lib/dinsro/data

RUN mkdir -p /dinsro/data
ADD target/dinsro.jar /dinsro.jar
ADD docker-config.edn /config.edn

EXPOSE 3000

CMD ["java", "-jar", "/dinsro.jar"]
