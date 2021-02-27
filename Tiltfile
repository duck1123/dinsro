# Tilt

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
