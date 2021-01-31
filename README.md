# dinsro

Sats-first personal finance management

[dinsro][1] - s1 is a treasury of money d1=s2.

[1]: http://jbovlaste.lojban.org/dict/dinsro

## Prerequisites

* clojure cli
* java
* yarn

Recommended:

* docker-compose

## Running

To start a web server for the application, run:

    make server-production

## Developing

* To start a web server for the application development, run:

        make server

  Then navigate to http://localhost:3000/

* To build and watch the front-end for changes, run:

        make watch-cljs

  Alternately, `C-c C-x C-c <enter> <enter> <enter>` and then probably a `y <enter>` in emacs.

## Testing

    make test

## Docker

   TODO: Document docker-based environment

         docker-compose up

## License

Copyright © 2019 Duck Nebuchadnezzar <duck@kronkltd.net>
