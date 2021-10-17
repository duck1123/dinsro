# Earthfile
FROM srghma/docker-dind-nixos:latest@sha256:d6b11f39ac5a4fcd11166f5830ee3a903a8d812404b3d6bbc99a92c5af4a0e6b
ARG nixos_image=nixos/nix@sha256:a6bcef50c7ca82ca66965935a848c8c388beb78c9a5de3e3b3d4ea298c95c708
ARG base_image=circleci/clojure:openjdk-11-tools-deps-node-browsers-legacy
ARG clojure_version=1.10.1.727
# https://github.com/clj-kondo/clj-kondo/releases
ARG kondo_version=2021.08.06
ARG node_version=14.15.3
# https://www.npmjs.com/package/npm?activeTab=versions
ARG npm_version=7.21.1

ARG repo=duck1123
ARG project=dinsro
ARG version=latest
ARG dev_group=circleci
ARG dev_user=circleci
ARG src_home=/usr/src/app
ARG data_dir=/var/lib/dinsro/data
ARG uid=3434
ARG gid=3434

WORKDIR /usr/src/app

EXPOSE_DOCKER_PORTS:
  COMMAND
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
  # shadow-cljs watcher
  EXPOSE 9630/tcp

IMPORT_JAR_DEPS:
  COMMAND
  COPY --dir +jar-deps/.clojure ${USER_HOME}
  COPY --dir +jar-deps/.deps.clj ${USER_HOME}
  COPY --dir +jar-deps/.m2 ${USER_HOME}
  COPY --dir +jar-deps/.cpcache .

INSTALL_BABASHKA:
  COMMAND
  # FIXME: This always downloads the latest version, enable pinning
  RUN curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install \
      && chmod +x install \
      && ./install \
      && rm -f install

INSTALL_CHROMIUM:
  COMMAND
  RUN apt update && apt install -y \
          chromium \
      && rm -rf /var/lib/apt/lists/*
  ENV CHROME_BIN=chromium

INSTALL_KONDO:
  COMMAND
  RUN curl -sLO https://raw.githubusercontent.com/clj-kondo/clj-kondo/master/script/install-clj-kondo \
      && chmod +x install-clj-kondo \
      && echo Version: $kondo_version \
      && ./install-clj-kondo --version $kondo_version \
      && rm -f install-clj-kondo

INSTALL_NODE:
  COMMAND
  RUN curl -fsSL https://deb.nodesource.com/setup_16.x | bash - \
      && apt-get install -y nodejs \
      && rm -rf /var/lib/apt/lists/*
  RUN npm install -g npm@${npm_version}
  # RUN npm install -g yarn
  RUN npm install -g karma-cli

CREATE_USER_NIX:
  COMMAND
  RUN addgroup -g ${gid} -S ${dev_group} && adduser -S ${dev_user} -G ${dev_group} -u ${uid}
  RUN chown -R ${uid}:${gid} ${src_home}

CREATE_USER_UBUNTU:
  COMMAND
  RUN addgroup --gid ${gid} ${dev_group} && adduser --ingroup ${dev_group} --uid ${uid} ${dev_user}
  RUN chown -R ${uid}:${gid} ${src_home}

base-builder-nix:
  FROM ${nixos_image}
  ENV USER_HOME=/home/${dev_user}
  WORKDIR /usr/src/app
  RUN nix-env -i autoconf
  RUN nix-env -i bash-5.1-p4
  RUN nix-env -i curl-7.74.0
  RUN nix-env -i openjdk-11.0.9+11
  RUN nix-env -i clojure-${clojure_version}
  RUN nix-env -i nodejs-${node_version}
  RUN nix-env -i xvfb-run
  RUN NIXPKGS_ALLOW_UNFREE=1 nix-env -i chromium
  ENV CHROME_BIN=chromium
  RUN mkdir -p /etc/fonts
  ENV FONTCONFIG_PATH=/etc/fonts
  DO +INSTALL_BABASHKA
  RUN nix-env -i tree
  DO +CREATE_USER_NIX
  RUN addgroup -g ${gid} -S ${dev_group} && adduser -S ${dev_user} -G ${dev_group} -u ${uid}
  RUN chown -R ${uid}:${gid} ${src_home}
  RUN nix-env -i clj-kondo
  # RUN nix-channel --add https://github.com/nix-community/home-manager/archive/master.tar.gz home-manager
  # RUN nix-channel --update
  USER ${uid}
  RUN mkdir -p ${USER_HOME}/.cache/yarn \
      && mkdir -p ${USER_HOME}/.m2 \
      && chown -R ${uid}:${gid} ${USER_HOME}

base-builder:
  FROM ${base_image}
  WORKDIR ${src_home}
  ENV USER_HOME=/home/${dev_user}
  USER root
  DO +INSTALL_NODE
  DO +INSTALL_CHROMIUM
  DO +INSTALL_BABASHKA
  DO +INSTALL_KONDO
  RUN chown -R ${uid}:${gid} ${src_home}
  RUN apt update && apt install -y \
          sudo \
          tree \
      && rm -rf /var/lib/apt/lists/*
  USER ${uid}

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
  WORKDIR ${src_home}
  RUN apk add curl
  RUN curl -L "https://github.com/docker/compose/releases/download/1.28.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
  && chmod +x /usr/local/bin/docker-compose

base-dind-builder-nix:
  RUN nix-env -i bash-5.0-p2
  RUN nix-env -i clojure-1.10.0.411
  RUN nix-env -i nodejs-10.15.0
  RUN nix-env -i openjdk-11.0.2-b9
  RUN addgroup -g ${gid} -S ${dev_group} && adduser -S ${dev_user} -G ${dev_group} -u ${uid}
  RUN chown -R ${uid}:${gid} ${src_home}
  USER ${uid}

builder:
  FROM +deps-builder
  RUN mkdir -p classes data target
  COPY --dir src/main src/main
  USER root
  RUN mkdir -p /var/lib/dinsro/data && chown -R ${uid}:${gid} /var/lib/dinsro/data
  USER ${uid}
  VOLUME ${data_dir}
  COPY resources/docker/config.edn config.edn
  COPY shadow-cljs.edn .

check:
  FROM +src
  COPY indentation.edn .
  RUN bb check

ci:
  BUILD +check
  BUILD +lint
  BUILD +test
  # BUILD +e2e
  BUILD +image

cert-downloader:
  FROM +base-builder
  USER root
  COPY resources/cert-downloader .
  RUN npm install
  ENTRYPOINT []
  CMD ["/bin/bash", "bootstrap.sh"]
  SAVE IMAGE ${repo}/cert-downloader:${version}

compile-frontend:
  FROM +src
  RUN bb compile-cljs
  SAVE ARTIFACT resources

compile-production:
  FROM +src
  COPY --dir src/prod src/prod
  RUN bb compile-production
  SAVE ARTIFACT classes

deps-builder:
  FROM +script-builder
  DO +IMPORT_JAR_DEPS

deps-dind-builder:
  FROM +base-dind-builder
  COPY package.json yarn.lock .
  COPY --dir bb.edn deps.edn script .
  DO +IMPORT_JAR_DEPS
  RUN --mount=type=cache,target=${USER_HOME}/.m2 \
      bb display-path || bb display-path

dev-image:
  FROM +deps-builder
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  # HEALTHCHECK CMD curl -f http://localhost:3000 || exit 1
  DO +EXPOSE_DOCKER_PORTS
  VOLUME ${data_dir}
  CMD ["bb", "dev-bootstrap"]
  SAVE IMAGE ${repo}/${project}:dev-${version}

dev-image-sources:
  FROM +dev-sources
  RUN bb compile-cljs
  HEALTHCHECK --start-period=600s CMD curl -f http://localhost:3000 || exit 1
  DO +EXPOSE_DOCKER_PORTS
  WORKDIR ${src_home}
  VOLUME ${data_dir}
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  USER root
  CMD ["bb", "dev-bootstrap"]
  SAVE IMAGE ${repo}/${project}:dev-sources-${version}

dev-sources:
  FROM +deps-builder
  COPY resources/docker/config.edn /etc/dinsro/config.edn
  ENV CONFIG_FILE=/etc/dinsro/config.edn
  COPY --dir . ${src_home}

e2e-base:
  FROM cypress/browsers
  ENV USER_HOME=/root
  RUN apt update && apt install -y \
      openjdk-11-jdk \
    && rm -rf /var/lib/apt/lists/*
  RUN apt update && apt install -y \
      docker.io \
    && rm -rf /var/lib/apt/lists/*
  RUN curl -O https://download.clojure.org/install/linux-install-1.10.2.790.sh \
    && chmod +x linux-install-1.10.2.790.sh \
    && ./linux-install-1.10.2.790.sh
  DO +INSTALL_BABASHKA
  COPY --dir +node-deps/node_modules node_modules
  DO +IMPORT_JAR_DEPS
  COPY cypress.json .
  RUN npx cypress install

e2e:
  FROM +e2e-base
  COPY . .
  RUN bb init
  RUN npx cypress install
  WITH DOCKER \
       --compose resources/cypress/docker-compose.yml \
       --service dinsro \
       --load ${repo}/${project}:dev-sources-${version}=+dev-image-sources
       RUN docker ps -a \
           && sh -c "docker logs -f cypress_dinsro_1" \
           & env | sort \
           && bb await-app \
           && bb test-integration
  END

e2e-dind:
  FROM +base-dind-builder
  COPY resources/cypres/docker-compose.yml docker-compose.yml
  COPY --dir bb.edn deps.edn script .
  RUN bb init
  WITH DOCKER \
      --compose docker-compose.yml \
      --service dinsro \
      --load ${repo}/${project}:e2e-${version}=+e2e-image \
      --load ${repo}/${project}:dev-sources-${version}=+dev-image-sources
      RUN bb await-app \
          && docker run --network=host ${repo}/${project}:e2e-${version}
  END

e2e-image:
  FROM cypress/browsers
  WORKDIR ${src_home}
  RUN apt update && apt install -y \
          openjdk-11-jdk \
      && rm -rf /var/lib/apt/lists/*
  COPY --dir +node-deps/node_modules node_modules
  DO +IMPORT_JAR_DEPS
  COPY cypress.json .
  RUN npx cypress install
  COPY --dir cypress .
  COPY --dir bb.edn deps.edn script .
  ENTRYPOINT []
  CMD ["bb", "test-integration"]
  SAVE IMAGE ${repo}/${project}:e2e-${version}

eastwood:
  FROM +dev-sources
  RUN bb eastwood

fileserver:
  FROM babashka/babashka:latest
  COPY resources/fileserver .
  RUN mkdir -p /mnt/lnd-data
  CMD ["bb", "watch.clj", "/mnt/lnd-data"]
  SAVE IMAGE ${repo}/lnd-fileserver:${version}

image:
  FROM openjdk:8-alpine
  VOLUME ${data_dir}
  COPY +jar/dinsro.jar dinsro.jar
  COPY resources/docker/config.edn config.edn
  CMD ["java", "-jar", "dinsro.jar"]
  SAVE IMAGE --push ${repo}/${project}:${version}

image-wait:
  FROM +image

jar:
  FROM +src
  COPY --dir +compile-production/classes .
  RUN bb compile-production-cljs
  RUN bb package-jar
  SAVE ARTIFACT target/app.jar /dinsro.jar AS LOCAL target/dinsro.jar

jar-deps:
  FROM +script-builder
  USER root
  RUN rm -rf ${USER_HOME}/.m2
  RUN --mount=type=cache,target=/root/.clojure \
      --mount=type=cache,target=/root/.m2 \
      --mount=type=cache,target=/root/.deps.clj \
      (bb display-path || bb display-path) \
      && bb init-bb \
      && cp -r /root/.clojure ${USER_HOME}/ \
      && cp -r /root/.deps.clj ${USER_HOME}/ \
      && cp -r /root/.m2 ${USER_HOME}/
  RUN chown -R ${uid} .cpcache
  RUN chown -R ${uid} ${USER_HOME}/.clojure
  RUN chown -R ${uid} ${USER_HOME}/.deps.clj
  RUN chown -R ${uid} ${USER_HOME}/.m2
  USER ${uid}
  SAVE ARTIFACT ${USER_HOME}/.clojure
  SAVE ARTIFACT ${USER_HOME}/.deps.clj
  SAVE ARTIFACT ${USER_HOME}/.m2
  SAVE ARTIFACT .cpcache

kibit:
  FROM +dev-sources
  RUN bb kibit

kondo:
  FROM +dev-sources
  RUN bb kondo

lint:
  BUILD +eastwood
  BUILD +kibit
  BUILD +kondo

node-deps:
  FROM +base-builder
  RUN pwd
  COPY package.json yarn.lock .
  RUN npx yarn install --frozen-lockfile
  SAVE ARTIFACT node_modules

script-builder:
  FROM +base-builder
  COPY package.json yarn.lock .
  COPY --dir +node-deps/node_modules node_modules
  COPY --dir bb.edn deps.edn script .

src:
  FROM +builder
  COPY --dir resources/main resources/

test:
  BUILD +test-clj
  BUILD +test-cljs

test-clj:
  FROM +test-sources
  RUN bb test-clj

test-cljs:
  FROM +test-sources
  RUN bb test-cljs

test-sources:
  FROM +src
  COPY tests.edn .
  COPY --dir src/test src
  DO +IMPORT_JAR_DEPS
  COPY karma.conf.js .
