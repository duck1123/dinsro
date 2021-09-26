# -*- mode: python -*-
# Tilt

base_url = 'dinsro.localhost'

load('ext://helm_remote', 'helm_remote')
load('ext://local_output', 'local_output')
load('ext://namespace', 'namespace_create')

disable_snapshots()
docker_prune_settings(disable=False)

# Create Namespaces
namespace_create('dinsro')

custom_build(
  'localhost:34371/duck1123/dinsro:dev-sources-latest',
  'earthly --build-arg repo=localhost:34371/duck1123 +dev-image-sources',
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

helm_yaml = helm(
  'resources/helm/dinsro',
  set = [
    'devtools.ingress.enabled=true',
    'devtools.ingress.hosts[0].host=devtools.' + base_url,
    'devtools.ingress.hosts[0].paths[0].path=/',
    'ingress.enabled=true',
    'ingress.hosts[0].host=' + base_url,
    'ingress.hosts[0].paths[0].path=/',
  ]
)

# docker_compose("./resources/tilt/dinsro/docker-compose.yml")
k8s_yaml(helm_yaml)
k8s_resource(
  workload='chart-dinsro',
  port_forwards = [
    port_forward(3333, 3333, name='cljs nrepl'),
    port_forward(3693, 3693, name='workspaces'),
    port_forward(7000, 7000, name='nRepl'),
    port_forward(9630, 9630, name='devtools')
  ],
  links = [
    link(base_url, 'Dinsro'),
  ],
  labels = [ 'Dinsro' ],
)

local_resource(
  'check',
  allow_parallel = True,
  cmd='bb check',
  deps = [ 'src' ],
  labels = [ 'format' ],
)

local_resource(
  'format',
  allow_parallel = True,
  auto_init = False,
  cmd='bb format',
  trigger_mode = TRIGGER_MODE_MANUAL,
  labels = [ 'format' ],
)

local_resource(
  'cypress',
  allow_parallel = True,
  auto_init = False,
  serve_cmd='npx cypress open',
  trigger_mode = TRIGGER_MODE_MANUAL,
  labels = [ 'test' ],
)

local_resource(
  'eastwood',
  allow_parallel = True,
  auto_init = False,
  cmd = 'bb eastwood',
  deps = [ 'src' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
  labels = [ 'lint' ],
)

local_resource(
  'karma',
  allow_parallel = True,
  auto_init = False,
  serve_cmd = 'npx karma start',
  trigger_mode = TRIGGER_MODE_MANUAL,
  links = [
    link('http://localhost:9876/debug.html', 'Debug'),
  ],
  labels = [ 'test' ],
)

local_resource(
  'kondo',
  allow_parallel = True,
  cmd='bb kondo',
  deps = [
    '.clj-kondo/config.edn',
    'src',
  ],
  labels = [ 'lint' ],
)

local_resource(
  'test-clj',
  allow_parallel = True,
  cmd = 'bb test-clj',
  deps = [ 'src/test' ],
  labels = [ 'test' ],
)

local_resource(
  'test-cljs',
  allow_parallel = True,
  cmd = 'bb test-cljs',
  deps = [ 'src/test' ],
  labels = [ 'test' ],
)

local_resource(
  'test-integration',
  allow_parallel = True,
  auto_init = False,
  cmd = 'npx cypress run',
  deps = [ 'src/test' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
  labels = [ 'test' ],
)
