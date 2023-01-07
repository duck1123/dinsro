# dinsro

Sats-first personal finance management

[dinsro][1] - s1 is a treasury of money d1=s2.

[1]: http://jbovlaste.lojban.org/dict/dinsro

## Disclaimer

At this moment, this server should in no way be considered secure. Do NOT publicly expose any instance
with connections to a mainnet node.

Use at your own risk!

## Prerequisites

* docker ([install](https://docs.docker.com/get-docker/))
* earthly ([install](https://earthly.dev/get-earthly))
* babashka ([install](https://github.com/babashka/babashka#installation))

If building locally, not using docker, you will need:

* clojure cli
* java
* yarn

Recommended:

* Tilt ([install](https://docs.tilt.dev/install.html))
* Helm ([install](https://helm.sh/docs/intro/install/))
* K3D ([install](https://k3d.io/#install-script))

A large number of requirements are available through nix.

``` shell
cp .envrc.example .envrc
```

## Tilt-based development

### Create Kubernetes cluster

You will need a kubernetes server to test with. For local development, I run the following command

``` shell
k3d cluster create \
  --api-port 6550 \
  -p "80:80@loadbalancer" \
  -p "443:443@loadbalancer" \
  --servers 1 \
  --registry-create registry \
  --kubeconfig-update-default
```
Consult my babashka script [here](https://github.com/duck1123/dotfiles/blob/master/bb.edn) for the latest settings.

### Configure Build

Create a file named `site.edn` and set with any desired customizations.

Refer to `site-defaults.edn` for options.

### Running Dev Build

To start tilt, run:

``` shell
tilt up
```

and then links to any of the deployed resources can be found at http://localhost:10350/

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

- [server](https://demo.dinsro.com/)
- [Docs](https://docs.dinsro.com/)
- [Notebooks](https://notebooks.demo.dinsro.com/)

## License

Copyright © 2019 Duck Nebuchadnezzar <duck@kronkltd.net>
