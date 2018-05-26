FROM java:8-alpine
MAINTAINER Daniel E. Renfer <duck@kronkltd.net>

ADD target/uberjar/dinsro.jar /dinsro/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/dinsro/app.jar"]
