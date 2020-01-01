# dinsro

Sats-first personal finance management

[dinsro][1] - s1 is a treasury of money d1=s2.

[1]: http://jbovlaste.lojban.org/dict/dinsro

## Prerequisites

You will need [Leiningen][2] 2.0 or above installed.

[2]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    script/server-production

## Developing

* To start a web server for the application development, run:

        script/server

  Then navigate to http://localhost:3000/

* To build and watch the front-end for changes, run:

        script/watch-javascript

  Alternately, `C-c C-x C-c <enter> <enter> <enter>` and then probably a `y <enter>` in emacs.

* To start devcards, runner:

        script/devcards

Then navigate to http://localhost:3450/devcards.html

## Testing

    script/test
    script/test-javascript

## Docker

To build a production-ready docker image, finish that feature for me.

It'll probably be something like:

    script/docker

## License

Copyright © 2019 Duck Nebuchadnezzar <duck@kronkltd.net>
