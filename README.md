# dinsro

Sats-first personal finance management

[dinsro][1] - s1 is a treasury of money d1=s2.

[1]: http://jbovlaste.lojban.org/dict/dinsro

## Disclaimer

At this moment, this server should in no way be considered secure. Do NOT publicly expose any instance
with connections to a mainnet node.

Use at your own risk!

## Prerequisites

* docker (https://docs.docker.com/get-docker/)
* earthly (https://earthly.dev/get-earthly)
* babashka (https://github.com/babashka/babashka#installation)

If building locally, not using docker, you will need:

* clojure cli
* java
* yarn

Recommended:

* Tilt (https://docs.tilt.dev/install.html)
* Helm (https://helm.sh/docs/intro/install/)
* K3D (https://k3d.io/#install-script)


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

### Configure Tilt

``` shell
cp tilt_config.json.example tilt_config.json
```

| key                 | description |
| ------------------- | ----------- |
| baseUrl             | The url the ingress will be listening for. There should be a wildcard dns entry for this domain pointing to your cluster |
| projectId           | If using Rancher, any created namespaces will be applied to this projectId |
| repo                | If using images in a nonstandard namespace |
| version             | allows the version number to be overridden |
| localDevtools       | If true, the devtools watcher will run locally |
| notebookInheritHost | Should the notebook domain be based off the baseUrl |
| notebookHost        | Host for notebooks if not inheriting from base |
| useLinting          | Should tilt run linting tasks |
| useNotebook         | Should a notebook be deployed |
| useNrepl            | expose nRepl servers |
| usePersistence      | Should a database backend be deployed |
| useProduction       | run with production build |
| useTests            | Should tilt run testing tasks |

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
(dinsro.seed/seed-db!)
```

## Bitcoin Resources

The seed data assumes that both the "alice" and "bob" users have lnd nodes.

To launch these resources for those users, run:

``` shell
bb helm-up alice
bb helm-up bob
```

Additional nodes can be created by substituting any name.

Each component of the above command can be substituted with:

``` shell
bb helm-bitcoin-apply ${USERNAME}
bb helm-lnd-apply ${USERNAME}
bb helm-specter-apply ${USERNAME}
bb helm-rtl-apply ${USERNAME}
bb helm-nbxplorer-apply ${USERNAME}
```

### Cleaning up

Nodes can be removed via:

``` shell
bb helm-clean ${USERNAME}
```
or

``` shell
bb helm-${SERVICE}-remove ${USERNAME}
```

## Testing

``` shell
bb test
```


## License

Copyright © 2019 Duck Nebuchadnezzar <duck@kronkltd.net>
