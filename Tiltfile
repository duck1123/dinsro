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
config.define_bool('useLnd1')
config.define_bool('useLnd2')
config.define_bool('useProduction')
config.define_bool('useRtl')

cfg            = config.parse()
base_url       = cfg.get('baseUrl',       'dinsro.localhost')
project_id     = cfg.get('projectId',     'p-vhkqf')
repo           = cfg.get('repo',          'duck1123')
version        = cfg.get('version',       'latest')
use_bitcoin    = cfg.get('useBitcoin',    True)
use_lnd1       = cfg.get('useLnd1',       True)
use_lnd2       = cfg.get('useLnd2',       True)
use_production = cfg.get('useProduction', False)
use_rtl        = cfg.get('useRtl',        True)

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
  'dinsro',
  annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
  labels = [ "field.cattle.io/projectId: %s" % project_id ],
)

if use_bitcoin:
  namespace_create(
    'bitcoin',
    annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
    labels = [ "field.cattle.io/projectId: %s" % project_id ],
  )
  k8s_yaml(helm(
    'resources/helm/fold/charts/bitcoind',
    name = 'bitcoind',
    namespace = 'bitcoin',
    values = [ 'resources/tilt/bitcoin_values.yaml' ],
  ))

if use_bitcoin and use_lnd1:
  namespace_create(
    'lnd1',
    annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
    labels = [ "field.cattle.io/projectId: %s" % project_id ],
  )
  k8s_yaml(helm(
    'resources/helm/fold/charts/lnd',
    name = 'lnd1',
    namespace = 'lnd1',
    values = [ 'resources/tilt/lnd1_values.yaml' ]
  ))

if use_bitcoin and use_lnd2:
  namespace_create(
    'lnd2',
    annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
    labels = [ "field.cattle.io/projectId: %s" % project_id ],
  )
  k8s_yaml(helm(
    'resources/helm/fold/charts/lnd',
    name = 'lnd2',
    namespace = 'lnd2',
    values = [ 'resources/tilt/lnd2_values.yaml' ]
  ))

if use_bitcoin and use_rtl:
  namespace_create(
    'rtl',
    annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
    labels = [ "field.cattle.io/projectId: %s" % project_id ],
  )

  k8s_yaml(helm(
    'resources/helm/rtl',
    namespace = 'rtl',
    set = [
      'certDownloader.image.repository=%s/cert-downloader' % repo,
    ],
  ))

k8s_yaml(helm(
  'resources/helm/dinsro',
  name = 'dinsro',
  namespace = 'dinsro',
  set = [
    'devtools.ingress.enabled=true',
    'devtools.ingress.hosts[0].host=devtools.' + base_url,
    'devtools.ingress.hosts[0].paths[0].path=/',
    'ingress.enabled=true',
    'ingress.hosts[0].host=' + base_url,
    'ingress.hosts[0].paths[0].path=/',
  ]
))

if use_production:
  namespace_create(
    'dinsro-production',
    annotations = [ "field.cattle.io/projectId: local:%s" % project_id ],
    labels = [ "field.cattle.io/projectId: %s" % project_id ],
  )

  k8s_yaml(helm(
    'resources/helm/dinsro',
    name = 'dinsro-production',
    namespace = 'dinsro-production',
    set = [
      'devtools.ingress.enabled=true',
      'devtools.ingress.hosts[0].host=devtools.' + 'dinsro-production.localhost',
      'devtools.ingress.hosts[0].paths[0].path=/',
      'ingress.enabled=true',
      'ingress.hosts[0].host=' + 'dinsro-production.localhost',
      'ingress.hosts[0].paths[0].path=/',
      'image.tag=' + version,
    ]
  ))

  custom_build(
    "%s/dinsro:%s" % (repo, version),
    "earthly --build-arg repo=%s +image" % repo,
    [
      'Earthfile',
      '.dockerignore',
      'bb.edn',
      'deps.edn',
      'src'
    ],
    tag = version,
  )

  k8s_resource(
    workload='dinsro-production',
    links = [
      link('dinsro-production.localhost', 'Dinsro'),
    ],
    labels = [ 'Dinsro' ],
  )

custom_build(
  "%s/dinsro:dev-sources-%s" % (repo, version),
  "earthly --build-arg repo=%s +dev-image-sources" % repo,
  [
    'Earthfile',
    '.dockerignore',
    'bb.edn',
    'deps.edn',
    'src'
  ],
  tag = "dev-sources-%s" % version,
  live_update=[
    sync('src', '/usr/src/app/src')
  ]
)

if use_bitcoin and (use_lnd1 or use_lnd2):
  custom_build(
    "%s/lnd-fileserver:%s" % (repo, version),
    "earthly --build-arg repo=%s +fileserver" % repo,
    [
      'Earthfile',
      'resources/fileserver',
    ],
    tag = version,
  )

if use_bitcoin and use_rtl:
  custom_build(
    "%s/cert-downloader:%s" % (repo, version),
    "earthly --build-arg repo=%s +cert-downloader" % repo,
    [
      'Earthfile',
      'resources/cert-downloader',
    ],
    tag = version,
  )

if use_bitcoin:
  k8s_resource(
    workload = 'bitcoind',
    labels = [ 'bitcoin' ],
  )

if use_bitcoin and use_lnd1:
  k8s_resource(
    workload='lnd1',
    links = [
      link('http://lnd1.localhost', 'lnd')
    ],
    labels = [ 'bitcoin' ],
  )

if use_bitcoin and use_lnd2:
  k8s_resource(
    workload='lnd2',
    links = [
      link('http://lnd2.localhost', 'lnd')
    ],
    labels = [ 'bitcoin' ],
  )

if use_bitcoin and use_rtl:
  k8s_resource(
    workload='rtl',
    links = [
      link('http://rtl.localhost/', 'RTL')
    ],
    labels = [ 'bitcoin' ],
  )
  k8s_resource(
    workload = 'cert-downloader',
    labels = [ 'bitcoin' ],
  )

k8s_resource(
  workload='dinsro',
  port_forwards = [
    port_forward(3333, 3333, name='cljs nrepl'),
    port_forward(3693, 3693, name='workspaces'),
    port_forward(7000, 7000, name='nRepl'),
    port_forward(9630, 9630, name='devtools')
  ],
  links = [
    link(base_url, 'Dinsro'),
    link('devtools.' + base_url, 'Devtools'),
    link('workspaces.' + base_url, 'Workspaces'),
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
