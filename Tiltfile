# Tilt

custom_build(
  'duck1123/dinsro:dev-sources-fulcro-latest',
  'earthly +dev-image-sources-fulcro',
  [
    'Earthfile',
    '.dockerignore',
    'Makefile'
  ],
  tag='dev-sources-fulcro-latest'
)

custom_build(
  'duck1123/dinsro:dev-sources-reframe-latest',
  'earthly +dev-image-sources-reframe',
  [
    'Earthfile',
    '.dockerignore',
    'Makefile'
  ],
  tag='dev-sources-reframe-latest'
)

docker_compose("./docker-compose.yml")

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
