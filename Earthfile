# Earthfile
FROM srghma/docker-dind-nixos:latest@sha256:d6b11f39ac5a4fcd11166f5830ee3a903a8d812404b3d6bbc99a92c5af4a0e6b
# ARG base_image=circleci/clojure:openjdk-11-tools-deps-node-browsers-legacy
ARG base_image=cimg/clojure:1.10-node
ARG clojure_version=1.10.1.727
# https://github.com/clj-kondo/clj-kondo/releases
ARG kondo_version=2021.12.16
ARG node_version=14.15.3
# https://www.npmjs.com/package/npm?activeTab=versions
ARG npm_version=8.3.1

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
  COPY --dir --chown=circleci +jar-deps/.clojure ${USER_HOME}
  COPY --dir --chown=root +jar-deps/.clojure /root
  COPY --dir --chown=circleci +jar-deps/.cpcache .
  COPY --dir --chown=circleci +jar-deps/.deps.clj ${USER_HOME}
  COPY --dir --chown=root +jar-deps/.deps.clj /root
  COPY --dir --chown=circleci +jar-deps/.m2 ${USER_HOME}
  COPY --dir --chown=root +jar-deps/.m2 /root

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
          chromium-browser \
      && rm -rf /var/lib/apt/lists/*
  ENV CHROME_BIN=chromium-browser

INSTALL_KONDO:
  COMMAND
  RUN curl -sLO https://raw.githubusercontent.com/clj-kondo/clj-kondo/master/script/install-clj-kondo \
      && chmod +x install-clj-kondo \
      && echo Version: $kondo_version \
      && ./install-clj-kondo --version $kondo_version \
      && rm -f install-clj-kondo

INSTALL_NODE:
  COMMAND
  RUN curl -fsSL https://deb.nodesource.com/setup_17.x | bash - \
      && apt-get install -y nodejs \
      && rm -rf /var/lib/apt/lists/*
  RUN npm install -g npm@${npm_version}
  # RUN npm install -g yarn
  RUN npm install -g karma-cli

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

babashka-base:
  FROM ${base_image}
  WORKDIR ${src_home}
  USER root
  DO +INSTALL_BABASHKA

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
  BUILD +cert-downloader
  BUILD +fileserver
  BUILD +portal
  BUILD +check
  BUILD +lint
  BUILD +test
  BUILD +image

cert-downloader:
  FROM babashka/babashka:latest
  ARG EXPECTED_REF=${repo}/cert-downloader:${version}
  COPY resources/cert-downloader .
  ENTRYPOINT ["bb", "bootstrap.clj"]
  CMD ["bb", "bootstrap.clj"]
  SAVE IMAGE ${EXPECTED_REF}

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

dev-image:
  FROM +deps-builder
  ARG EXPECTED_REF=${repo}/${project}:dev-${version}
  DO +EXPOSE_DOCKER_PORTS
  VOLUME ${data_dir}
  CMD ["bb", "dev-bootstrap"]
  SAVE IMAGE ${EXPECTED_REF}

dev-image-sources-base:
  ARG watch_sources=true
  FROM +dev-sources-minimal
  USER root
  IF [ "$watch_sources" = "true" ]
    USER $uid
    RUN bb compile-cljs
  END
  HEALTHCHECK --start-period=600s CMD curl -f http://localhost:3000 || exit 1
  DO +EXPOSE_DOCKER_PORTS
  WORKDIR ${src_home}
  VOLUME ${data_dir}
  USER root
  CMD ["bb", "dev-bootstrap"]

dev-image-sources:
  ARG watch_sources=true
  FROM +dev-image-sources-base --build-arg watch_sources=$watch_sources
  ARG EXPECTED_REF=${repo}/${project}:dev-sources-${version}
  SAVE IMAGE ${EXPECTED_REF}

dev-sources:
  FROM +deps-builder
  COPY resources/docker/config.edn /etc/dinsro/config.edn
  COPY --dir . ${src_home}

dev-sources-minimal:
  FROM +deps-builder
  COPY resources/docker/config.edn /etc/dinsro/config.edn
  COPY --dir src ${src_home}
  COPY shadow-cljs.edn site.edn .
  COPY --dir resources/main ${src_home}/resources/main
  COPY --dir resources/workspaces ${src_home}/resources/workspaces

eastwood:
  FROM +dev-sources
  RUN bb eastwood

fileserver:
  FROM babashka/babashka:latest
  ARG EXPECTED_REF=${repo}/lnd-fileserver:${version}
  COPY resources/fileserver .
  RUN mkdir -p /mnt/lnd-data
  CMD ["bb", "watch.clj", "/mnt/lnd-data"]
  SAVE IMAGE ${EXPECTED_REF}

image:
  FROM openjdk:17-alpine
  ARG EXPECTED_REF=${repo}/${project}:${version}
  VOLUME ${data_dir}
  RUN mkdir -p src
  COPY +jar/dinsro.jar dinsro.jar
  COPY resources/docker/config.edn config.edn
  COPY --dir src/main src/notebooks src/shared src
  CMD ["java", "-jar", "dinsro.jar"]
  SAVE IMAGE --push ${EXPECTED_REF}

image-wait:
  FROM +image

jar:
  FROM +src
  COPY --dir +compile-production/classes .
  RUN bb compile-production-cljs
  RUN bb package-jar
  SAVE ARTIFACT target/dinsro-4.0.null.jar /dinsro.jar AS LOCAL target/dinsro.jar

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
  # BUILD +eastwood
  # BUILD +kibit
  BUILD +kondo

node-deps:
  FROM +base-builder
  COPY package.json semantic.json yarn.lock .
  # RUN npx yarn add fomantic-ui --ignore-scripts
  RUN npx yarn install --frozen-lockfile
  SAVE ARTIFACT node_modules

portal:
  FROM +babashka-base
  ARG EXPECTED_REF=${repo}/portal:${version}
  # RUN apk add java
  COPY resources/portal .
  ENTRYPOINT ["bb", "portal.clj"]
  CMD ["bb", "portal.clj"]
  RUN bb portal.clj --dry-run
  SAVE IMAGE ${EXPECTED_REF}

script-builder:
  FROM +base-builder
  COPY package.json semantic.json yarn.lock .
  COPY --dir +node-deps/node_modules node_modules
  COPY --dir bb.edn build.clj deps.edn rollup.config.js .
  COPY --dir src/babashka src/shared src

src:
  FROM +builder
  COPY --dir resources/main resources/

test:
  BUILD +test-clj

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
