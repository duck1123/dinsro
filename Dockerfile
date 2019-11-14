# FROM openjdk:8-alpine
# MAINTAINER Daniel E. Renfer <duck@kronkltd.net>

# ADD target/uberjar/dinsro.jar /dinsro/app.jar

# EXPOSE 3000

# CMD ["java", "-jar", "/dinsro/app.jar"]
# 1
FROM clojure
# 2
LABEL maintainer="Divyum Rastogi"
# 3
COPY . /usr/src/app
# 4
WORKDIR /usr/src/app
# 5
EXPOSE 8080

RUN lein deps

# 6
CMD ["lein", "run"]
