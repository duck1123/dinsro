# Earthfile
VERSION 0.7
FROM alpine:3.15
ARG --global base_image=cimg/clojure:1.10
ARG --global clojure_version=1.10.1.727
# https://github.com/clj-kondo/clj-kondo/releases
ARG --global kondo_version=2023.10.20
# https://nodejs.org/en/download
ARG --global NODE_MAJOR=20
# https://www.npmjs.com/package/npm?activeTab=versions
ARG --global npm_version=10.2.5
# https://github.com/tilt-dev/tilt/releases
ARG --global tilt_version=0.33.9

# ARG repo=duck1123
# ARG project=dinsro
ARG --global version=latest
ARG --global dev_group=circleci
ARG --global dev_user=circleci
ARG --global src_home=/usr/src/app
ARG --global data_dir=/var/lib/dinsro/data
ARG --global uid=3434
ARG --global gid=3434

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
  COPY --dir --chown=circleci --chmod=755 \
       +jar-deps/.clojure \
       +jar-deps/.deps.clj \
       +jar-deps/.gitlibs \
       +jar-deps/.m2 \
       ${USER_HOME}
  COPY --dir --chown=root --chmod=755 \
       +jar-deps/.clojure \
       +jar-deps/.deps.clj \
       +jar-deps/.gitlibs \
       +jar-deps/.m2 \
       /root
  COPY --dir --chown=circleci +jar-deps/.cpcache .

INSTALL_BABASHKA:
  COMMAND
  # FIXME: This always downloads the latest version, enable pinning
  RUN curl -sLO https://raw.githubusercontent.com/babashka/babashka/master/install \
      && chmod +x install \
      && ./install \
      && rm -f install

INSTALL_BYOBU:
  COMMAND
  RUN apt update && apt install -y \
      byobu \
    && rm -rf /var/lib/apt/lists/*

INSTALL_FIREFOX:
  COMMAND
  RUN apt update && apt install -y \
      firefox-geckodriver \
    && rm -rf /var/lib/apt/lists/*

INSTALL_KONDO:
  COMMAND
  RUN curl -sLO https://raw.githubusercontent.com/clj-kondo/clj-kondo/master/script/install-clj-kondo \
      && chmod +x install-clj-kondo \
      && echo Version: $kondo_version \
      && ./install-clj-kondo --version $kondo_version \
      && rm -f install-clj-kondo

# Assumes running as root
INSTALL_NODE:
  COMMAND
  RUN mkdir -p /etc/apt/keyrings
  RUN curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg
  RUN echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_${NODE_MAJOR}.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list
  RUN apt update \
      && apt install -y nodejs \
      && rm -rf /var/lib/apt/lists/*
  RUN npm install -g npm@${npm_version}

INSTALL_NOSCL:
  COMMAND
  RUN curl -LJO "https://github.com/fiatjaf/noscl/releases/download/v0.6.0/noscl"
  RUN mv noscl /usr/local/bin/noscl
  RUN chmod +x /usr/local/bin/noscl
  RUN mkdir -p /root/.config/noscl

INSTALL_TILT:
  COMMAND
  RUN echo ${tilt_version}
  RUN curl -fsSL "https://github.com/tilt-dev/tilt/releases/download/v${tilt_version}/tilt.${tilt_version}.linux.x86_64.tar.gz" \
        | tar -xzv tilt \
      && sudo mv tilt /usr/local/bin/tilt

INSTALL_TILT_LATEST:
  COMMAND
  RUN curl -fsSL https://raw.githubusercontent.com/tilt-dev/tilt/master/scripts/install.sh | bash

main-pipeline:
  PIPELINE
  TRIGGER push master
  TRIGGER pr master
  TRIGGER push main
  TRIGGER pr main
  BUILD +test

all-images:
  BUILD +support-images
  BUILD +image

base-builder:
  FROM ${base_image}
  WORKDIR ${src_home}
  ENV USER_HOME=/home/${dev_user}
  USER root
  DO +INSTALL_NODE
  DO +INSTALL_BABASHKA
  DO +INSTALL_KONDO
  DO +INSTALL_NOSCL
  RUN chown -R ${uid}:${gid} ${src_home}
  RUN apt update && apt install -y \
          sudo \
          nmap \
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
  COPY --dir src/main src/notebooks src/notebook-utils src
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
  BUILD +validate
  BUILD +all-images

cert-downloader:
  FROM babashka/babashka:latest
  ARG repo=duck1123
  ARG project=cert-downloader
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:${tag}
  COPY resources/cert-downloader .
  ENTRYPOINT ["bb", "bootstrap.clj"]
  CMD ["bb", "bootstrap.clj"]
  SAVE IMAGE --push ${EXPECTED_REF}

compile-frontend:
  FROM +src
  RUN bb compile-cljs
  SAVE ARTIFACT resources

compile-production:
  FROM +src
  COPY --dir src/prod src/prod
  COPY +compile-styles/* resources/main/public/
  RUN bb compile-production
  SAVE ARTIFACT classes

compile-styles:
  FROM +deps-builder
  COPY --dir src/styles src/styles
  COPY semantic/src/theme.config semantic/src/theme.config
  COPY semantic/src/collections/menu.variables semantic/src/site/collections/
  COPY semantic/src/site/globals/site.* semantic/src/site/globals/
  RUN bb install-style-dependencies
  RUN bb compile-styles
  SAVE ARTIFACT resources/main/public/css/ css/
  SAVE ARTIFACT resources/main/public/themes/ themes/

deps-builder:
  FROM +script-builder
  DO +IMPORT_JAR_DEPS

dev-image:
  FROM +deps-builder
  COPY --dir \
       src \
       site-defaults.edn \
       semantic \
       package.json \
       shadow-cljs.edn \
       .

  COPY --dir resources/main resources/main
  DO +EXPOSE_DOCKER_PORTS
  VOLUME ${data_dir}
  USER root
  CMD ["bb", "dev-bootstrap"]

image-dev:
  FROM +dev-image
  ARG repo=duck1123
  ARG project=dinsro
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:dev-${version}
  SAVE IMAGE ${EXPECTED_REF}

devcards-image:
  FROM +dev-sources-minimal
  ARG repo=duck1123
  ARG project=dinsro
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:devcards-${version}
  COPY --dir resources/tilt resources
  COPY --dir public .
  CMD ["bb", "devcards-server"]
  SAVE IMAGE --push ${EXPECTED_REF}

devspace-base:
  ARG repo=duck1123
  ARG project=devimage
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:${tag}
  FROM +base-builder
  USER root
  DO +INSTALL_BYOBU
  DO +INSTALL_TILT
  RUN apt update && apt install -y \
      bash-completion \
      inetutils-ping \
      zsh \
    && rm -rf /var/lib/apt/lists/*
  DO +IMPORT_JAR_DEPS
  COPY --dir +node-deps/node_modules node_modules
  RUN chsh -s /bin/zsh
  RUN mkdir -p /root/.byobu
  RUN echo "set -g default-shell /usr/bin/zsh\nset -g default-command /usr/bin/zsh" > /root/.byobu/.tmux.conf
  SAVE IMAGE --push ${EXPECTED_REF}

dev-image-sources-base:
  ARG watch_sources=true
  ARG use_notebooks=false
  FROM +dev-sources-minimal
  USER root
  RUN mkdir -p resources/main/public/css \
      && mkdir -p resources/main/public/js \
      && mkdir -p resources/main/public/themes \
      && chown -R $uid resources/main
  IF [ "$watch_sources" = "true" ]
    USER $uid
    RUN --no-cache ls -al resources
    RUN bb compile-cljs
  END
  HEALTHCHECK --start-period=600s CMD curl -f http://localhost:3000 || exit 1
  DO +EXPOSE_DOCKER_PORTS
  WORKDIR ${src_home}
  VOLUME ${data_dir}
  USER root
  CMD ["bb", "dev-bootstrap"]

dev-image-sources:
  ARG use_notebooks=false
  ARG watch_sources=true
  ARG repo=duck1123
  ARG project=dinsro
  ARG tag=latest
  FROM +dev-image-sources-base \
       --build-arg watch_sources=$watch_sources \
       --build-arg use_notebooks=$use_notebooks
  ARG EXPECTED_REF=${repo}/${project}:dev-sources-${version}
  COPY --dir resources/main ${src_home}/resources
  SAVE IMAGE ${EXPECTED_REF}

dev-sources:
  FROM +deps-builder
  COPY resources/docker/config.edn /etc/dinsro/config.edn
  COPY --dir . ${src_home}
  COPY --dir +node-deps/node_modules node_modules
  DO +IMPORT_JAR_DEPS

dev-sources-minimal:
  FROM +deps-builder
  COPY resources/docker/config.edn /etc/dinsro/config.edn
  COPY --dir src ${src_home}
  COPY shadow-cljs.edn .
  COPY --dir resources/workspaces ${src_home}/resources/workspaces

docs:
  FROM +dev-sources-minimal
  RUN bb docs
  SAVE ARTIFACT target/doc docs

docs-image:
  FROM babashka/babashka:latest
  ARG repo=duck1123
  ARG project=dinsro
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:docs-${version}
  WORKDIR /usr/src/app
  COPY resources/docs-server .
  COPY --dir +docs/docs .
  RUN ls -al
  CMD ["bb", "serve.clj"]
  SAVE IMAGE --push ${EXPECTED_REF}

eastwood:
  FROM +dev-sources
  RUN bb eastwood

fileserver:
  FROM babashka/babashka:latest
  ARG repo=duck1123
  ARG project=lnd-fileserver
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:${tag}
  COPY resources/fileserver .
  RUN mkdir -p /mnt/lnd-data
  CMD ["bb", "watch.clj", "/mnt/lnd-data"]
  SAVE IMAGE --push ${EXPECTED_REF}

image:
  FROM amazoncorretto:17-alpine
  ARG repo=duck1123
  ARG project=dinsro
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:${tag}
  RUN echo ${EXPECTED_REF}
  WORKDIR ${src_home}
  VOLUME ${data_dir}
  RUN mkdir -p src
  COPY +jar/dinsro.jar dinsro.jar
  COPY resources/docker/config.edn /etc/dinsro/config.edn
  COPY --dir src/main src/notebooks src/notebook-utils src/shared src
  COPY seed.edn .
  CMD ["java", "-jar", "dinsro.jar"]
  SAVE IMAGE --push ${EXPECTED_REF}

image-wait:
  FROM +image

jar:
  FROM +src
  COPY --dir +compile-production/classes .
  COPY +compile-styles/* resources/main/public/
  COPY --dir resources/main/public/images resources/main/public/
  COPY resources/main/*.conf resources/main
  RUN bb compile-production-cljs
  RUN bb package-jar
  SAVE ARTIFACT target/dinsro-4.0.null.jar /dinsro.jar AS LOCAL target/dinsro.jar

jar-deps:
  FROM +script-builder
  USER root
  RUN rm -rf ${USER_HOME}/.m2
  RUN --mount=type=cache,target=/root/.clojure \
      --mount=type=cache,target=/root/.gitlibs \
      --mount=type=cache,target=/root/.m2 \
      --mount=type=cache,target=/root/.deps.clj \
      (bb display-path || bb display-path) \
      && bb init-bb \
      && cp -r /root/.clojure ${USER_HOME}/ \
      && cp -r /root/.deps.clj ${USER_HOME}/ \
      && cp -r /root/.gitlibs ${USER_HOME}/ \
      && cp -r /root/.m2 ${USER_HOME}/
  USER ${uid}
  SAVE ARTIFACT ${USER_HOME}/.clojure
  SAVE ARTIFACT ${USER_HOME}/.deps.clj
  SAVE ARTIFACT ${USER_HOME}/.gitlibs
  SAVE ARTIFACT ${USER_HOME}/.m2
  SAVE ARTIFACT .cpcache

kibit:
  FROM +dev-sources
  RUN bb kibit

kondo:
  FROM +dev-sources
  RUN bb kondo

lint:
  BUILD +kondo
  BUILD +eastwood
  # BUILD +kibit

node-deps:
  FROM +base-builder
  COPY package.json semantic.json yarn.lock .
  # RUN npx yarn add fomantic-ui --ignore-scripts
  RUN npx yarn install --frozen-lockfile
  SAVE ARTIFACT node_modules

nostream-build:
  FROM node:18-alpine3.16
  ARG NOSTREAM_BASE=resources/nostream/
  WORKDIR /build
  COPY \
    ${NOSTREAM_BASE}package.json \
    ${NOSTREAM_BASE}package-lock.json \
    ${NOSTREAM_BASE}knexfile.js \
    .
  COPY --dir ${NOSTREAM_BASE}migrations /build
  RUN npm install -g knex@2.3.0
  RUN npm install knex --quiet
  RUN npm install --quiet
  COPY ${NOSTREAM_BASE} .
  RUN npm run build
  SAVE ARTIFACT dist

nostream-image:
  FROM node:18
  ARG NOSTREAM_BASE=resources/nostream/
  ARG NOSTREAM_IMAGE_BASE=resources/nostream-image/
  ARG NOSTR_CONFIG_DIR=/home/node/.nostr
  ARG repo=duck1123
  ARG project=nostream
  ARG tag=latest
  ARG EXPECTED_REF=${repo}/${project}:${tag}
  LABEL org.opencontainers.image.title="Nostr Typescript Relay"
  LABEL org.opencontainers.image.source=https://github.com/Cameri/nostr-ts-relay
  LABEL org.opencontainers.image.description="nostr-ts-relay"
  LABEL org.opencontainers.image.authors="Ricardo Arturo Cabral Mejía"
  LABEL org.opencontainers.image.licenses=MIT
  WORKDIR /app
  RUN apt update && apt install -y \
        curl \
        gnupg2 \
        jq \
        postgresql-client \
        tree \
        wait-for-it \
        wget \
    && rm -rf /var/lib/apt/lists/*
  COPY +nostream-build/dist .
  RUN npm install --omit=dev --quiet
  USER 1000:1000
  COPY ${NOSTREAM_IMAGE_BASE}bootstrap.sh ${NOSTREAM_IMAGE_BASE}check-connection .
  COPY --dir ${NOSTREAM_BASE}resources ${NOSTREAM_BASE}migrations ${NOSTREAM_BASE}knexfile.js .
  RUN mkdir -p $NOSTR_CONFIG_DIR
  CMD ["./bootstrap.sh"]
  ENTRYPOINT ["./bootstrap.sh"]
  SAVE IMAGE --push ${EXPECTED_REF}

script-builder:
  FROM +base-builder
  COPY package.json semantic.json yarn.lock .
  COPY --dir +node-deps/node_modules node_modules
  COPY --dir bb.edn build.clj deps.edn rollup.config.js .
  COPY --dir src/babashka src/shared src

src:
  FROM +builder

support-images:
  BUILD +cert-downloader
  BUILD +fileserver
  BUILD +devspace-base
  BUILD +devcards-image
  BUILD +docs-image

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
  USER root
  DO +INSTALL_FIREFOX
  USER ${uid}:${gid}
  COPY --dir tests.edn bin .
  COPY --dir src/test src
  DO +IMPORT_JAR_DEPS

validate:
  BUILD +check
  BUILD +test
  BUILD +lint
