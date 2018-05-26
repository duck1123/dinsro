FROM java:8-alpine
MAINTAINER Your Name <you@example.com>

ADD target/uberjar/dinsro.jar /dinsro/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/dinsro/app.jar"]
