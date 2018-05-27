FROM ubuntu

MAINTAINER Daniel E. Renfer <duck@kronkltd.net>

ENV GOPATH /root/go
ENV APP_HOME ${GOPATH}/src/github.com/duck1123/dinsro
WORKDIR ${APP_HOME}

# gpg keys listed at https://github.com/nodejs/node#release-team
RUN set -ex \
  && for key in \
    94AE36675C464D64BAFA68DD7434390BDBE9B9C5 \
    FD3A5288F042B6850C66B31F09FE44734EB7990E \
    71DCFD284A79C3B38668286BC97EC7A07EDE3FC1 \
    DD8F2338BAE7501E3DD5AC78C273792F7D83545D \
    C4F0DFFF4E8C1A8236409D08E73BC641CC11F4C8 \
    B9AE9905FFD7803F25714661B63B535A4C206CA9 \
    56730D5401028683275BD23C23EFEFE93C4CFFFE \
    77984A986EBC2AA786BC0F66B01FBB92821C587A \
  ; do \
    gpg --keyserver pgp.mit.edu --recv-keys "$key" || \
    gpg --keyserver keyserver.pgp.com --recv-keys "$key" || \
    gpg --keyserver ha.pool.sks-keyservers.net --recv-keys "$key" ; \
  done

RUN set -x \
    && apt-get update \
    && apt-get install -y \
       software-properties-common \
       python-software-properties \
    && add-apt-repository ppa:gophers/archive \
    && apt-get update \
    && apt-get install -y \
       byobu \
       build-essential \
       curl \
       git \
       golang-1.9 \
       jq \
       netcat \
    && rm -rf /var/lib/apt/lists/* \
    && ln -s /usr/lib/go-1.9/bin/go /usr/bin/go

RUN set -x \
    && curl -sL https://deb.nodesource.com/setup_6.x | bash - \
    && curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | apt-key add - \
    && echo "deb https://dl.yarnpkg.com/debian/ stable main" | tee /etc/apt/sources.list.d/yarn.list \
    && apt-get update \
    && apt-get install -y \
       byobu \
       build-essential \
       curl \
       git \
       jq \
       netcat \
       nodejs \
       yarn \
    && rm -rf /var/lib/apt/lists/*

COPY . ${APP_HOME}
