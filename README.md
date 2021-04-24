# dinsro

Sats-first personal finance management

[dinsro][1] - s1 is a treasury of money d1=s2.

[1]: http://jbovlaste.lojban.org/dict/dinsro

## Prerequisites

* docker (https://docs.docker.com/get-docker/)
* earthly (https://earthly.dev/get-earthly)
* babashka (https://github.com/babashka/babashka#installation)

If building locally, not using docker, you will need:

* clojure cli
* java
* yarn

Recommended:

* docker-compose
* Tilt (https://docs.tilt.dev/install.html)

## Running

To start a web server for the application, run:

``` shell
bb server-production
```

## Developing

* To start a web server for the application development, run:

``` shell
bb dev
```

  Then navigate to http://web-dinsro.docker.localhost:8081/

  To view status of frontend, go to http://localhost:8080/dashboard/#/

  To view shadow-cljs builds, go to http://watch-dinsro.docker.localhost:8081/dashboard

  To view workspaces, go to http://workspaces-dinsro.docker.localhost:8081/

* To build and watch the front-end for changes, run:

``` shell
bb watch-cljs

```

## Testing

``` shell
bb test
```


## License

Copyright © 2019 Duck Nebuchadnezzar <duck@kronkltd.net>
