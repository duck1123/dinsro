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
  'check',
  allow_parallel = True,
  cmd='bb check',
  deps = [ 'src' ],
)

local_resource(
  'format',
  allow_parallel = True,
  auto_init = False,
  cmd='bb format',
  trigger_mode = TRIGGER_MODE_MANUAL,
)

local_resource(
  'cypress',
  allow_parallel = True,
  auto_init = False,
  serve_cmd='npx cypress open',
  trigger_mode = TRIGGER_MODE_MANUAL,
)

local_resource(
  'eastwood',
  allow_parallel = True,
  auto_init = False,
  cmd = 'bb eastwood',
  deps = [ 'src' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
)

local_resource(
  'kondo',
  allow_parallel = True,
  cmd='bb kondo',
  deps = [ 'src' ],
)

local_resource(
  'test-clj',
  allow_parallel = True,
  cmd = 'bb test-clj',
  deps = [ 'src/test' ],
)

local_resource(
  'test-cljs',
  allow_parallel = True,
  cmd = 'bb test-cljs',
  deps = [ 'src/test' ],
)

local_resource(
  'test-integration',
  allow_parallel = True,
  auto_init = False,
  cmd = 'npx cypress run',
  deps = [ 'src/test' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
)
