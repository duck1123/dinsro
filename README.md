# dinsro

Sats-first personal finance management

[dinsro][1] - s1 is a treasury of money d1=s2.

[1]: http://jbovlaste.lojban.org/dict/dinsro

## Disclaimer

At this moment, this server should in no way be considered secure. Do NOT publicly expose any instance
with connections to a mainnet node.

Use at your own risk!

## Setup

For up to date setup information, check the [Runbook](./runbook.org)

## Running Without Tilt

To start a web server for the application, run:

``` shell
bb server-production
```

To build a production docker image, run:

``` shell
bb build-image
```

## Developing Without Tilt

* To build and watch the front-end for changes, run:

``` shell
bb watch-cljs

```

* To start a web server for the application development, run:

``` shell
bb run
```

Then navigate to http://localhost:3000/

To view status of frontend, go to http://localhost:9630/

To view workspaces, go to http://localhost:9631/

# Docker

``` shell
docker run -p 3000:3000 duck1123/dinsro:latest
```

## Seeding data

Test data can be loaded by running

``` shell
bb seed
```

or, from a repl:

``` clojure
(dinsro.components.seed/seed-db!)
```

## Testing

``` shell
bb test
```

## Live Demo

- [server](https://dinsro.com/)
- [Docs](https://docs.dinsro.com/)
- [Notebooks](https://notebooks.dinsro.com/)

## License

Copyright © 2023 Duck Nebuchadnezzar <duck@kronkltd.net>
