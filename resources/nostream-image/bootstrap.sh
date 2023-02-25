#!/usr/bin/env bash

set +e

ls -al

ls -al build

wait-for-it ${DB_HOST?}:${DB_PORT?} -- ./check-connection

npm run db:migrate

node src/index.js
