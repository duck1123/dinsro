# Tilt

custom_build(
  'localhost:34371/duck1123/dinsro:dev-sources-latest',
  'earthly +dev-image-sources',
  [
    'Earthfile',
    '.dockerignore',
    'bb.edn',
    'src'
  ],
  tag = 'dev-sources-latest',
  live_update=[
    sync('src', '/usr/src/app/src')
  ]
)

# docker_compose("./resources/tilt/dinsro/docker-compose.yml")
k8s_yaml(helm('resources/helm/dinsro'))
k8s_resource(
  workload='chart-dinsro',
  port_forwards = [
    port_forward(3333, 3333, name='cljs nrepl'),
    port_forward(3693, 3693, name='workspaces'),
    port_forward(7000, 7000, name='nRepl'),
    port_forward(9630, 9630, name='devtools')
  ],
  links = [
    link('dinsro.localhost', 'Dinsro')
  ]
)

local_resource(
    'kondo',
    cmd='bb kondo',
    deps = [ 'src' ],
    allow_parallel=True
)

local_resource(
  'check',
  cmd='bb check',
  deps = [ 'src' ],
  allow_parallel = True
)

local_resource(
  'test',
  cmd = 'bb test',
  allow_parallel = True,
  deps = [
    'src/test',
  ]
)
