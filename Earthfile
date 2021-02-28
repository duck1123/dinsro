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
  BUILD +e2e

ci-reframe:
  BUILD +lint-reframe
  BUILD +test-reframe
  BUILD +e2e-reframe

ci-fulcro:
  BUILD +lint-reframe
  BUILD +test-reframe
  BUILD +e2e-reframe

compile-frontend-fulcro:
  FROM +src-fulcro-ubuntu
  RUN make compile-cljs-fulcro
  SAVE ARTIFACT resources

compile-frontend-reframe:
  FROM +src-reframe-ubuntu
  RUN make compile-cljs-reframe
  SAVE ARTIFACT resources

compile-production:
  BUILD +compile-production-fulcro
  BUILD +compile-production-reframe

compile-production-fulcro:
  FROM +src-fulcro-ubuntu
  COPY --dir src/prod src/prod
  RUN make compile-production-fulcro
  SAVE ARTIFACT classes

compile-production-reframe:
  FROM +src-reframe-ubuntu
  COPY --dir src/prod src/prod
  RUN make compile-production-reframe
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
      make display-path-fulcro || make display-path-fulcro

dev-builder:
  FROM +deps-builder-ubuntu

dev-image:
  BUILD +dev-image-fulcro
  BUILD +dev-image-reframe

dev-image-fulcro:
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
  CMD ["make", "dev-fulcro-bootstrap"]
  SAVE IMAGE duck1123/dinsro:dev-fulcro-latest

dev-image-reframe:
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
  CMD ["make", "dev-reframe-bootstrap"]
  SAVE IMAGE duck1123/dinsro:dev-reframe-latest

dev-image-sources-fulcro:
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
  CMD ["make", "dev-bootstrap-fulcro"]
  SAVE IMAGE duck1123/dinsro:dev-sources-fulcro-latest

dev-image-sources-reframe:
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
  CMD ["make", "dev-bootstrap-reframe"]
  SAVE IMAGE duck1123/dinsro:dev-sources-reframe-latest

dev-sources-fulcro:
  FROM +dev-builder
  CMD ["zsh"]
  COPY dev-image-config.edn /etc/dinsro/config.edn
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  COPY . /usr/src/app

dev-sources-reframe:
  FROM +dev-builder
  CMD ["zsh"]
  COPY dev-image-config.edn /etc/dinsro/config.edn
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  COPY . /usr/src/app

e2e:
  BUILD +e2e-fulcro
  BUILD +e2e-reframe

e2e-fulcro:
  FROM +base-dind-builder
  COPY e2e-docker-compose.yml docker-compose.yml
  COPY Makefile .
  RUN make init
  WITH DOCKER \
      --compose docker-compose.yml \
      --service fulcro \
      --load duck1123/dinsro:e2e-latest-fulcro=+e2e-image-fulcro \
      --load duck1123/dinsro:dev-sources-fulcro-latest=+dev-image-sources-fulcro
      RUN docker ps -a \
          && make await-app \
          && docker run --network=host duck1123/dinsro:e2e-latest-fulcro \
              make test-integration-fulcro
      END

e2e-reframe:
  FROM +base-dind-builder
  COPY e2e-docker-compose.yml docker-compose.yml
  COPY Makefile .
  RUN make init
  WITH DOCKER \
      --compose docker-compose.yml \
      --service reframe \
      --load duck1123/dinsro:e2e-latest-reframe=+e2e-image-reframe \
      --load duck1123/dinsro:dev-sources-reframe-latest=+dev-image-sources-reframe
  RUN docker ps -a \
      && make await-app \
      && docker run --network=host duck1123/dinsro:e2e-latest-reframe make test-integration-reframe
  END

e2e-image-fulcro:
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
  CMD ["make", "test-integration-fulcro"]
  SAVE IMAGE duck1123/dinsro:e2e-latest-fulcro

e2e-image-reframe:
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
  CMD ["make", "test-integration-reframe"]
  SAVE IMAGE duck1123/dinsro:e2e-latest-fulcro

image-fulcro:
  FROM openjdk:8-alpine
  VOLUME /var/lib/dinsro/data
  COPY +jar-fulcro/dinsro.jar dinsro.jar
  COPY docker-config.edn config.edn
  CMD ["java", "-jar", "dinsro.jar"]
  SAVE IMAGE duck1123/dinsro:latest-fulcro

image-reframe:
  FROM openjdk:8-alpine
  VOLUME /var/lib/dinsro/data
  COPY +jar-reframe/dinsro.jar dinsro.jar
  COPY docker-config.edn config.edn
  CMD ["java", "-jar", "dinsro.jar"]
  SAVE IMAGE duck1123/dinsro:latest-reframe

jar-deps:
  FROM +base-builder-ubuntu
  COPY Makefile deps.edn .
  USER root
  RUN rm -rf /home/dinsro/.m2
  RUN --mount=type=cache,target=/root/.m2 \
      (make display-path-fulcro || make display-path-fulcro) \
      && cp -r /root/.m2 /home/dinsro/
  RUN chown -R 1000 /home/dinsro/.m2
  USER 1000
  SAVE ARTIFACT /home/dinsro/.m2

jar-fulcro:
  FROM +src-fulcro-ubuntu
  COPY --dir +compile-production-fulcro/classes .
  RUN make package-jar-fulcro
  SAVE ARTIFACT target/app.jar /dinsro.jar

jar-reframe:
  FROM +src-reframe-ubuntu
  COPY --dir +compile-production-reframe/classes .
  RUN make build-production-reframe
  SAVE ARTIFACT target/app.jar /dinsro.jar

lint:
  BUILD +lint-fulcro
  BUILD +lint-reframe

lint-fulcro:
  FROM +dev-sources-fulcro
  RUN make lint-fulcro

lint-reframe:
  FROM +dev-sources-reframe
  RUN make lint-reframe

lint-kondo:
  BUILD +lint-kondo-fulcro
  BUILD +lint-kondo-reframe

lint-kondo-fulcro:
  FROM +dev-sources-fulcro
  RUN make lint-kondo-fulcro

lint-kondo-reframe:
  FROM +dev-sources-reframe
  RUN make lint-kondo-reframe

node-deps:
  FROM +base-builder-ubuntu
  COPY package.json yarn.lock .
  RUN npx yarn install --frozen-lockfile
  SAVE ARTIFACT node_modules

src-fulcro:
  FROM +builder
  COPY --dir resources/fulcro resources
  COPY --dir src/fulcro src

src-fulcro-ubuntu:
  FROM +builder-ubuntu
  COPY --dir resources/fulcro resources
  COPY --dir src/fulcro src

src-reframe:
  FROM +builder
  COPY --dir resources/reframe resources/reframe
  COPY --dir src/reframe src

src-reframe-ubuntu:
  FROM +builder-ubuntu
  COPY --dir resources/reframe resources/reframe
  COPY --dir src/reframe src

test:
  BUILD +test-fulcro-ubuntu
  BUILD +test-reframe-ubuntu

test-fulcro:
  BUILD +test-fulcro-clj
  BUILD +test-fulcro-cljs

test-fulcro-clj:
  FROM +test-sources-fulcro
  RUN make test-fulcro-clj

test-fulcro-cljs:
  FROM +test-sources-fulcro
  RUN make test-fulcro-cljs

test-fulcro-ubuntu:
  FROM +src-fulcro-ubuntu
  COPY --dir src/test src/fulcro-test src
  COPY --dir +jar-deps/.m2 /home/dinsro/
  COPY karma.conf.js .
  RUN make test-fulcro

test-reframe:
  FROM +src-reframe
  COPY --dir src/test src/reframe-test src
  COPY --dir +jar-deps/.m2 /home/dinsro/
  COPY karma.conf.js .
  RUN make test-reframe

test-reframe-ubuntu:
  FROM +src-reframe-ubuntu
  COPY --dir src/test src/reframe-test src
  COPY --dir +jar-deps/.m2 /home/dinsro/
  COPY karma.conf.js .
  RUN make test-reframe

test-sources-fulcro:
  FROM +src-fulcro
  COPY --dir src/test src/fulcro-test src
  COPY --dir +jar-deps/.m2 /home/dinsro/
  COPY karma.conf.js .
