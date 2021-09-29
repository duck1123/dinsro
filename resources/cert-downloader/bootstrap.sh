#!/bin/sh

set -e

echo "Bootstrapping cert downloader"

npm install

rm -f \
   /mnt/data/lnd1/tls.cert.bak \
   /mnt/data/lnd1/admin.macaroon.bak \
   /mnt/data/lnd2/tls.cert.bak \
   /mnt/data/lnd2/admin.macaroon.bak

mkdir -p /mnt/data

mkdir -p /mnt/data/lnd1
wget -O /mnt/data/lnd1/tls.cert.bak http://lnd1-internal.lnd1/tls.cert
wget -O /mnt/data/lnd1/admin.macaroon.bak http://lnd1-internal.lnd1/admin.macaroon || true

mkdir -p /mnt/data/lnd2
wget -O /mnt/data/lnd2/tls.cert.bak http://lnd2-internal.lnd2/tls.cert
wget -O /mnt/data/lnd2/admin.macaroon.bak http://lnd2-internal.lnd2/admin.macaroon || true

if [ -s "/mnt/data/lnd1/tls.cert.bak" ]; then
  mv /mnt/data/lnd1/tls.cert.bak /mnt/data/lnd1/tls.cert
else
  rm -f /mnt/data/lnd1/tls.cert
  exit -1
fi

if [ -s "/mnt/data/lnd2/tls.cert.bak" ]; then
  mv /mnt/data/lnd2/tls.cert.bak /mnt/data/lnd2/tls.cert
else
  rm -f /mnt/data/lnd2/tls.cert
  exit -1
fi

if [ -s "/mnt/data/lnd1/admin.macaroon.bak" ]; then
  mv /mnt/data/lnd1/admin.macaroon.bak /mnt/data/lnd1/admin.macaroon
else
  rm -f /mnt/data/lnd1/admin.macaroon
  rm -f /mnt/data/lnd1/admin.macaroon.bak
  echo "Node 1 not created yet"
fi

if [ -s "/mnt/data/lnd2/admin.macaroon.bak" ]; then
  mv /mnt/data/lnd2/admin.macaroon.bak /mnt/data/lnd2/admin.macaroon
else
  rm -f /mnt/data/lnd2/admin.macaroon
  rm -f /mnt/data/lnd2/admin.macaroon.bak
  echo "Node 2 not created yet"
fi

tree /mnt/data/

sleep infinity
