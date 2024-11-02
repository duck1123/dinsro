# Setup

These are the perquisites before running

## Install Dependencies

### Docker

```sh {"name":"install-docker"}
sudo apt install docker-ce
```

### k3d

This is used to create a local kubernetes cluster

https://k3d.io/v5.7.4/#installation

```sh {"name":"install-k3d"}
wget -q -O - https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash
```

### Earthly

This is used for building images

https://earthly.dev/get-earthly

```sh {"name":"install-earthly"}
sudo /bin/sh -c 'wget https://github.com/earthly/earthly/releases/latest/download/earthly-linux-amd64 -O /usr/local/bin/earthly \
  && chmod +x /usr/local/bin/earthly \
  && /usr/local/bin/earthly bootstrap --with-autocomplete'
```

### Devspace

#### Install

This is used for deploying resources to your local dev cluster

https://www.devspace.sh/docs/getting-started/installation

```sh {"name":"install-devspace"}
curl -L -o devspace "https://github.com/loft-sh/devspace/releases/latest/download/devspace-linux-amd64" \
  && sudo install -c -m 0755 devspace /usr/local/bin
```

#### Set Aliases

This is an optional step.

```sh
alias d="devspace"
alias dr="devspace run"
```

### Clojure

https://clojure.org/guides/install_clojure#_linux_instructions

```sh {"name":"install-clojure"}
CLOJURE_VERSION=1.11.1.1208 \
curl -O https://download.clojure.org/install/linux-install-${CLOJURE_VERSION}.sh \
  && chmod +x linux-install-${CLOJURE_VERSION}.sh \
  && sudo ./linux-install-${CLOJURE_VERSION}.sh
```

## Create Cluster Environment

### Create Registry

This will create a registry to share created images with the cluster

```sh {"name":"create-registry"}
k3d registry create myregistry.localtest.me --port 12345
```

### Create Cluster

This will create a single node kubernetes cluster in a docker container and update the default kubeconfig

```sh {"name":"create-cluster"}
k3d cluster create \
  --api-port 6550 \
  -p "80:80@loadbalancer" \
  -p "443:443@loadbalancer" \
  --k3s-arg "--disable=traefik@server:0" \
  --servers 1 \
  --registry-use k3d-myregistry.localtest.me:12345 \
  --kubeconfig-update-default
```

### install cert-manager

https://cert-manager.io/docs/installation/kubectl/

#### install cert manager manifests

Install cert-manager CRDs into the cluster.

```sh {"name":"install-cert-manager"}
kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.0/cert-manager.yaml
```

#### Create letsencrypt provider

Create cluster issuer record.

This will cause any ingress with the appropriate annotations to obtain a
certificate from letsencrypt

```sh {"name":"install-cluster-issuer"}
EMAIL=someuser@example.com cat <<EOF | kubectl apply -f -
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt-prod
spec:
  acme:
    server: https://acme-v02.api.letsencrypt.org/directory
    email: ${EMAIL}
    privateKeySecretRef:
      name: letsencrypt-prod
    solvers:
    - http01:
        ingress:
          class: traefik
EOF
```

### Create namespace

```sh {"name":"create-namespace"}
kubectl create namespace dinsro
```

# Build

## Deploy

```sh {"name":"deploy-devspace"}
devspace deploy
```

# Dev

## If running inside the devimage fails

```sh
rm -rf node_modules
bb install
```

# Running

## Docker

```sh {"name":"run-docker"}
docker run -it -p 3000:3000 duck1123/dinsro:latest
```

# Cleanup

## Delete Registry

Delete registry for locally-built images

```sh {"name":"delete-registry"}
k3d registry delete k3d-myregistry.localtest.me
```

## Delete Cluster

Completely destroy dev cluster

```sh {"name":"delete-cluster"}
k3d cluster delete k3s-default
```

## Earthly

Clears all earthly caches

```sh {"name":"prune-earthly"}
earthly prune
```

## Remove devimage

Removes devimage from pod and resets to default

```sh {"name":"remove-devimage"}
devspace reset pods
```
