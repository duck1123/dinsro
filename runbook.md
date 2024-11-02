# Setup

These are the perquisites before running

## Install Dependencies

### Docker

```sh
sudo apt install docker-ce
```

### k3d

https://k3d.io/v5.4.6/#installation

```sh
wget -q -O - https://raw.githubusercontent.com/k3d-io/k3d/main/install.sh | bash
```

### Earthly

https://earthly.dev/get-earthly

```sh
  sudo /bin/sh -c 'wget https://github.com/earthly/earthly/releases/latest/download/earthly-linux-amd64 -O /usr/local/bin/earthly \
       && chmod +x /usr/local/bin/earthly \
       && /usr/local/bin/earthly bootstrap --with-autocomplete'
```

### Devspace

#### Install

https://www.devspace.sh/docs/getting-started/installation

```sh
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

```sh
CLOJURE_VERSION=1.11.1.1208 \
curl -O https://download.clojure.org/install/linux-install-${CLOJURE_VERSION}.sh \
  && chmod +x linux-install-${CLOJURE_VERSION}.sh \
  && sudo ./linux-install-${CLOJURE_VERSION}.sh
```

## Create Cluster Environment

### Create Registry

This will create a registry to share created images with the cluster

```sh
k3d registry create myregistry.localtest.me --port 12345
```

### Create Cluster

This will create a single node kubernetes cluster in a docker container

```sh
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

```sh
  kubectl apply -f https://github.com/cert-manager/cert-manager/releases/download/v1.12.0/cert-manager.yaml
```

#### Create letsencrypt provider

```sh
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

```sh
kubectl create namespace dinsro
```

# Build

## Deploy

```sh
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

```sh
docker run -it -p 3000:3000 duck1123/dinsro:latest
```

# Cleanup

## Delete Registry

```sh
k3d registry delete k3d-myregistry.localtest.me
```

## Delete Cluster

```shell
k3d cluster delete k3s-default
```

## Earthly

``` sh
earthly prune
```
