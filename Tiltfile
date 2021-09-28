# -*- mode: python -*-
# Tilt

# Base Url for the dinsro instance
config.define_string('baseUrl')
# Rancher project id to assign namespaces to
config.define_string('procjectId')
# Repo for docker images
config.define_string('repo')
# Version for built images
config.define_string('version')
# Deploy core node
config.define_bool('useBitcoin')

cfg         = config.parse()
base_url    = cfg.get('baseUrl',   'dinsro.dev.kronkltd.net')
project_id  = cfg.get('projectId', 'p-vhkqf')
repo        = cfg.get('repo',      'duck1123')
version     = cfg.get('baseUrl',   'latest')
use_bitcoin = cfg.get('useBitcoin', True)

load('ext://helm_remote', 'helm_remote')
load('ext://local_output', 'local_output')
load('ext://namespace', 'namespace_create')

disable_snapshots()
docker_prune_settings(
  disable = False,
  num_builds = 2,
  keep_recent = 2,
)

# Create Namespaces
namespace_create(
  'bitcoin',
  annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
  labels = [ "field.cattle.io/projectId: %s" % project_id ],
)
namespace_create(
  'dinsro',
  annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
  labels = [ "field.cattle.io/projectId: %s" % project_id ],
)

if use_bitcoin:
  k8s_yaml(helm(
    'resources/helm/fold/charts/bitcoind',
    name = 'bitcoind',
    namespace = 'bitcoin',
    values = [ 'resources/tilt/bitcoin_values.yaml' ],
  ))

custom_build(
  "%s/dinsro:dev-sources-%s" % (repo, version),
  "earthly --build-arg repo=%s +dev-image-sources" % repo,
  [
    'Earthfile',
    '.dockerignore',
    'bb.edn',
    'src'
  ],
  tag = "dev-sources-%s" % version,
  live_update=[
    sync('src', '/usr/src/app/src')
  ]
)

k8s_yaml(helm(
  'resources/helm/dinsro',
  set = [
    'devtools.ingress.enabled=true',
    'devtools.ingress.hosts[0].host=devtools.' + base_url,
    'devtools.ingress.hosts[0].paths[0].path=/',
    'ingress.enabled=true',
    'ingress.hosts[0].host=' + base_url,
    'ingress.hosts[0].paths[0].path=/',
  ]
))

if use_bitcoin:
  k8s_resource(
    workload = 'bitcoind',
    labels = [ 'bitcoin' ],
  )

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
