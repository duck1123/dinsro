# Earthfile
FROM srghma/docker-dind-nixos:latest@sha256:d6b11f39ac5a4fcd11166f5830ee3a903a8d812404b3d6bbc99a92c5af4a0e6b
WORKDIR /usr/src/app

base-builder:
  FROM +base-builder-ubuntu

base-builder-nix:
  FROM nixos/nix@sha256:a6bcef50c7ca82ca66965935a848c8c388beb78c9a5de3e3b3d4ea298c95c708
  WORKDIR /usr/src/app
  RUN nix-env -i autoconf
  RUN nix-env -i gnumake-4.2.1
  RUN nix-env -i bash-5.1-p4
  RUN nix-env -i curl-7.74.0
  RUN nix-env -i openjdk-11.0.9+11
  RUN nix-env -i clojure-1.10.1.727
  RUN nix-env -i nodejs-14.15.3
  RUN nix-env -i xvfb-run
  RUN NIXPKGS_ALLOW_UNFREE=1 nix-env -i chromium
  ENV CHROME_BIN=chromium
  RUN mkdir -p /etc/fonts
  ENV FONTCONFIG_PATH=/etc/fonts
  RUN nix-env -i tree
  RUN addgroup -g 1000 -S dinsro && adduser -S dinsro -G dinsro -u 1000
  RUN chown -R 1000:1000 /usr/src/app
  RUN nix-env -i clj-kondo
  RUN nix-channel --add https://github.com/nix-community/home-manager/archive/master.tar.gz home-manager
  RUN nix-channel --update
  USER "1000"
  RUN mkdir -p ${HOME}/.cache/yarn && mkdir -p ${HOME}/.m2 && chown -R 1000:1000 /home/dinsro

base-builder-ubuntu:
  FROM clojure:tools-deps
  WORKDIR /usr/src/app
  RUN curl -fsSL https://deb.nodesource.com/setup_15.x | bash - \
      && apt-get install -y nodejs \
      && rm -rf /var/lib/apt/lists/*
  RUN apt update && apt install -y \
          tree \
      && rm -rf /var/lib/apt/lists/*
  RUN apt update && apt install -y \
          chromium \
  && rm -rf /var/lib/apt/lists/*
  ENV CHROME_BIN=chromium
  RUN addgroup --gid 1000 dinsro && adduser --ingroup dinsro --uid 1000 dinsro
  RUN chown -R 1000:1000 /usr/src/app
  USER "1000"
  RUN mkdir -p ${HOME}/.cache/yarn && mkdir -p ${HOME}/.m2 && chown -R 1000:1000 /home/dinsro

base-cypress-dind:
  FROM earthly/dind:ubuntu
  RUN apt update && DEBIAN_FRONTEND=noninteractive apt-get install -y \
          keyboard-configuration \
      && rm -rf /var/lib/apt/lists/*
  RUN apt update && DEBIAN_FRONTEND=noninteractive apt-get install -y \
          libgtk2.0-0 \
          libgtk-3-0 \
          libgbm-dev \
      && rm -rf /var/lib/apt/lists/*
  RUN apt update && apt-get install -y \
          libnotify-dev \
          libgconf-2-4 \
          libnss3 \
      && rm -rf /var/lib/apt/lists/*
  RUN apt update && apt-get install -y \
          libxss1 \
          libasound2 \
          libxtst6 \
      && rm -rf /var/lib/apt/lists/*
  RUN apt update && apt-get install -y \
          xauth \
          xvfb \
      && rm -rf /var/lib/apt/lists/*
  RUN apt update && apt install -y \
          openjdk-11-jdk \
      && rm -rf /var/lib/apt/lists/*
  RUN curl -O https://download.clojure.org/install/linux-install-1.10.2.790.sh \
      && chmod +x linux-install-1.10.2.790.sh \
      && ./linux-install-1.10.2.790.sh
  RUN curl -fsSL https://deb.nodesource.com/setup_15.x | bash - \
      && apt-get install -y nodejs \
      && rm -rf /var/lib/apt/lists/*

base-dind-builder:
  FROM earthly/dind:alpine
  WORKDIR /usr/src/app
  RUN apk add curl
  RUN apk add make
  RUN curl -L "https://github.com/docker/compose/releases/download/1.28.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
  && chmod +x /usr/local/bin/docker-compose

base-dind-builder-nix:
  RUN nix-env -i bash-5.0-p2
  RUN nix-env -i clojure-1.10.0.411
  RUN nix-env -i gnumake-4.2.1
  RUN nix-env -i nodejs-10.15.0
  RUN nix-env -i openjdk-11.0.2-b9
  RUN addgroup -g 1000 -S dinsro && adduser -S dinsro -G dinsro -u 1000
  RUN chown -R 1000:1000 /usr/src/app
  USER "1000"

builder:
  FROM +deps-builder
  RUN mkdir -p classes data target
  COPY --dir lib .
  COPY --dir src/main src/main
  COPY docker-config.edn config.edn
  COPY shadow-cljs.edn .

builder-ubuntu:
  FROM +deps-builder-ubuntu
  RUN mkdir -p classes data target
  COPY --dir lib .
  COPY --dir src/main src/main
  COPY docker-config.edn config.edn
  COPY shadow-cljs.edn .

ci:
  BUILD +lint
  BUILD +test
  # BUILD +e2e

compile-frontend:
  FROM +src-ubuntu
  RUN make compile-cljs
  SAVE ARTIFACT resources

compile-production:
  FROM +src-ubuntu
  COPY --dir src/prod src/prod
  RUN make compile-production
  SAVE ARTIFACT classes

deps-builder:
  FROM +base-builder
  RUN mkdir -p ${HOME}/.cache/yarn && mkdir -p ${HOME}/.m2 && chown -R 1000:1000 /home/dinsro
  COPY package.json yarn.lock .
  COPY --dir +node-deps/node_modules node_modules
  COPY Makefile deps.edn .
  COPY --dir +jar-deps/.m2 /home/dinsro/
  USER root
  RUN chown -R 1000 /home/dinsro/.m2
  USER 1000

deps-builder-ubuntu:
  FROM +base-builder-ubuntu
  RUN mkdir -p ${HOME}/.cache/yarn && mkdir -p ${HOME}/.m2 && chown -R 1000:1000 /home/dinsro
  COPY package.json yarn.lock .
  COPY --dir +node-deps/node_modules node_modules
  COPY Makefile deps.edn .
  COPY --dir +jar-deps/.m2 /home/dinsro/
  USER root
  RUN chown -R 1000 /home/dinsro/.m2
  USER 1000

deps-dind-builder:
  FROM +base-dind-builder
  COPY package.json yarn.lock .
  COPY Makefile deps.edn .
  COPY --dir +jar-deps/.m2 /home/dinsro/
  RUN --mount=type=cache,target=/home/dinsro/.m2 \
      make display-path || make display-path

dev-builder:
  FROM +deps-builder-ubuntu

dev-image:
  FROM +dev-builder
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  HEALTHCHECK CMD curl -f http://localhost:3000 || exit 1
  # Main web interface
  EXPOSE 3000/tcp
  # nRepl interface (cljs)
  EXPOSE 3333/tcp
  # Main cljs devtools
  EXPOSE 3691/tcp
  # Tests
  EXPOSE 3692/tcp
  # Workspaces
  EXPOSE 3693/tcp
  # nRepl interface (clj)
  EXPOSE 7000/tcp
  VOLUME /var/lib/dinsro/data
  COPY docker-config.edn config.edn
  CMD ["make", "dev-bootstrap"]
  SAVE IMAGE duck1123/dinsro:dev-latest

dev-image-sources:
  FROM +dev-builder
  HEALTHCHECK --start-period=600s CMD curl -f http://localhost:3000 || exit 1
  # Main web interface
  EXPOSE 3000/tcp
  # nRepl interface (cljs)
  EXPOSE 3333/tcp
  # Main cljs devtools
  EXPOSE 3691/tcp
  # Tests
  EXPOSE 3692/tcp
  # Workspaces
  EXPOSE 3693/tcp
  # nRepl interface (clj)
  EXPOSE 7000/tcp
  WORKDIR /usr/src/app
  VOLUME /var/lib/dinsro/data
  COPY dev-image-config.edn /etc/dinsro/config.edn
  COPY . /usr/src/app
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  CMD ["make", "dev-bootstrap"]
  SAVE IMAGE duck1123/dinsro:dev-sources-latest

dev-sources:
  FROM +dev-builder
  CMD ["zsh"]
  COPY dev-image-config.edn /etc/dinsro/config.edn
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  COPY . /usr/src/app

e2e:
  FROM +base-dind-builder
  COPY e2e-docker-compose.yml docker-compose.yml
  COPY Makefile .
  RUN make init
  WITH DOCKER \
      --compose docker-compose.yml \
      --service dinsro \
      --load duck1123/dinsro:e2e-latest=+e2e-image \
      --load duck1123/dinsro:dev-sources-latest=+dev-image-sources
      RUN docker ps -a \
          && make await-app \
          && docker run --network=host duck1123/dinsro:e2e-latest \
              make test-integration
      END

e2e-image:
  FROM cypress/browsers
  RUN apt update && apt install -y \
          openjdk-11-jdk \
      && rm -rf /var/lib/apt/lists/*
  RUN --mount=type=cache,target=/home/dinsro/.cache \
  npx yarn install --frozen-lockfile
  COPY cypress.json .
  RUN npx cypress install
  COPY --dir cypress .
  COPY Makefile .
  ENTRYPOINT []
  CMD ["make", "test-integration"]
  SAVE IMAGE duck1123/dinsro:e2e-latest

image:
  FROM openjdk:8-alpine
  VOLUME /var/lib/dinsro/data
  COPY +jar/dinsro.jar dinsro.jar
  COPY docker-config.edn config.edn
  CMD ["java", "-jar", "dinsro.jar"]
  SAVE IMAGE duck1123/dinsro:latest

jar-deps:
  FROM +base-builder-ubuntu
  COPY Makefile deps.edn .
  USER root
  RUN rm -rf /home/dinsro/.m2
  RUN --mount=type=cache,target=/root/.m2 \
      (make display-path || make display-path) \
      && cp -r /root/.m2 /home/dinsro/
  RUN chown -R 1000 /home/dinsro/.m2
  USER 1000
  SAVE ARTIFACT /home/dinsro/.m2

jar:
  FROM +src-ubuntu
  COPY --dir +compile-production/classes .
  RUN make package-jar
  SAVE ARTIFACT target/app.jar /dinsro.jar

lint:
  BUILD +lint-eastwood
  BUILD +lint-kibit
  BUILD +lint-kondo

lint-eastwood:
  FROM +dev-sources
  RUN make lint-eastwood

lint-kibit:
  FROM +dev-sources
  RUN make lint-kibit

lint-kondo:
  FROM +dev-sources
  RUN make lint-kondo

node-deps:
  FROM +base-builder-ubuntu
  COPY package.json yarn.lock .
  RUN npx yarn install --frozen-lockfile
  SAVE ARTIFACT node_modules

src:
  FROM +builder
  COPY --dir resources/main resources

src-ubuntu:
  FROM +builder-ubuntu
  COPY --dir resources/main resources

test:
  BUILD +test-clj
  BUILD +test-cljs

test-clj:
  FROM +test-sources
  RUN make test-clj

test-cljs:
  FROM +test-sources
  RUN make test-cljs

test-ubuntu:
  FROM +src-ubuntu
  COPY --dir src/test src
  COPY --dir +jar-deps/.m2 /home/dinsro/
  COPY karma.conf.js .
  RUN make test

test-sources:
  FROM +src
  COPY --dir src/test src
  COPY --dir +jar-deps/.m2 /home/dinsro/
  COPY karma.conf.js .
