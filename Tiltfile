# Tilt

custom_build(
  'duck1123/dinsro:dev-sources-latest',
  'earthly +dev-image-sources',
  [
    'Earthfile',
    '.dockerignore',
    'Makefile'
  ],
  tag='dev-sources-latest'
)

docker_compose("./tilt-docker-compose.yml")
# k8s_yaml('k8s/deployment.yaml')

local_resource(
    'kondo',
    cmd='make lint-kondo',
    deps=['src'],
    allow_parallel=True
)

local_resource(
    'check',
    cmd='make check',
    deps=['src'],
    allow_parallel=True
)
