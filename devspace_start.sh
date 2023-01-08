#!/usr/bin/env bash
set +ex

# Include project's bin/ folder in PATH
export PATH="./bin:$PATH"

chsh -s /bin/bash root

yarn install

byobu-ctrl-a screen
tilt analytics opt out

source <(tilt completion bash)
source <(devspace completion bash)

byobu new-session "tilt up --legacy=true --host=0.0.0.0"
