# -*- mode: python -*-
# Tilt

load('ext://local_output', 'local_output')
load('ext://namespace', 'namespace_create')
load('ext://uibutton', 'cmd_button')

# Files used to generate config
watch_file('bb.edn')
watch_file('site.edn')
watch_file('site-defaults.edn')
watch_file('src/babashka')

config_data = decode_json(local_output('bb tilt-config'))
# dinsro_values = local_output('bb generate-dinsro-values')

def config_get(key):
  v = config_data.get(key)
  if (not v == None):
    return v
  else:
    return config_data[key]

base_url        = config_get('baseUrl')
project_id      = config_get('projectId')
repo            = config_get('repo')
version         = config_get('version')
local_devtools  = config_get('localDevtools')
notebook_host   = config_get('notebookHost')
use_linting     = config_get('useLinting')
use_notebook    = config_get('useNotebook')
use_nrepl       = config_get('useNrepl')
use_persistence = config_get('usePersistence')
use_production  = config_get('useProduction')
use_tests       = config_get('useTests')
use_docs        = False

portal_url = 'http://' + config_get('portalHost')

def get_notebook_host():
  return "notebook." + base_url if config_get('notebookInheritHost') else notebook_host

def earthly_build(
    ref,
    target,
    build_args={},
    ci=False,
    deps=[],
    disable_push=False,
    pass_expected_ref=True,
    push=False,
    live_update=[],
    skips_local_docker=False,
    use_cache=True
):
  cmd = ["earthly"]

  for arg, value in build_args.items():
    cmd += ['--build-arg', arg + '=' + value]

  if pass_expected_ref:
    cmd += ['--build-arg', 'EXPECTED_REF=$EXPECTED_REF']

  if push:
    cmd += ['--push']

  if ci:
    cmd += ['--ci']

  if not use_cache:
    cmd += ['--no-cache']

  cmd += [target];
  command = ' '.join(cmd)
  custom_build(
    ref=ref,
    command=command,
    deps=deps,
    live_update=live_update,
    disable_push=disable_push,
    skips_local_docker=skips_local_docker,
  )

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

devtools_host = ("devtools.%s" % base_url) if not local_devtools else "localhost:9630"

local_resource(
  'dinsro-values',
  allow_parallel = True,
  cmd='bb generate-dinsro-values',
  deps = [ 'site.edn' ],
  labels = [ 'compile' ],
)

k8s_yaml(helm(
  'resources/helm/dinsro',
  name = 'dinsro',
  namespace = 'dinsro',
  values=["./target/dinsro_values.yaml"],
))

if use_production:
  custom_build(
    "%s/dinsro:%s" % (repo, version),
    "earthly --build-arg repo=%s --build-arg EXPECTED_REF=$EXPECTED_REF +image" % repo,
    [
      'Earthfile',
      '.dockerignore',
      'bb.edn',
      'deps.edn',
      'src'
    ],
  )

custom_build(
  "%s/portal:%s" % (repo, version),
  "earthly --build-arg repo=%s --build-arg EXPECTED_REF=$EXPECTED_REF +portal" % repo,
  [
    'Earthfile',
    '.dockerignore',
    'bb.edn',
    "resources/portal",
    'deps.edn',
  ],
)

if not use_production:
  custom_build(
    "%s/dinsro:dev-sources-%s" % (repo, version),
    " ".join([
      'earthly',
      "--build-arg repo=%s" % repo,
      "--build-arg watch_sources=%s" % ('false' if local_devtools else 'true'),
      '--build-arg EXPECTED_REF=$EXPECTED_REF',
      '+dev-image-sources',
    ]),
    [
      'Earthfile',
      '.dockerignore',
      'bb.edn',
      'deps.edn',
      'resources/docker',
      'resources/main/public',
      'site.edn',
      'src',
      "tilt_config.json",
    ],
    live_update=[
      sync('resources/main/public', '/usr/src/app/resources/main/public'),
      sync('site.edn', '/usr/src/app/site.edn'),
      sync('src', '/usr/src/app/src'),
      sync('tilt_config.json', '/usr/src/app/tilt_config.json'),
    ]
  )

has_devtools = not (local_devtools or use_production)

k8s_resource(
  workload='dinsro',
  port_forwards = [x for x in [
    port_forward(3333, 3333, name='cljs nrepl') if has_devtools else None,
    port_forward(3693, 3693, name='workspaces') if has_devtools else None,
    port_forward(7000, 7000, name='nRepl') if use_nrepl else None,
    port_forward(9630, 9630, name='devtools') if has_devtools else None,
  ] if x != None],
  links = [x for x in [
    link(base_url, 'dinsro'),
    link('devtools.' + base_url, 'Devtools') if has_devtools else None,
    link('workspaces.' + base_url, 'Workspaces') if has_devtools else None,
    link(get_notebook_host(), 'Notebook') if use_notebook else None,
  ] if x != None],
  labels = [ 'dinsro' ],
)

if use_persistence:
  k8s_resource(
    workload = 'postgres',
    labels = [ 'database' ],
  )

if use_linting:
  local_resource(
    'check',
    allow_parallel = True,
    cmd='bb check',
    deps = [ 'src' ],
    labels = [ 'lint' ],
  )

if use_linting:
  local_resource(
    'format',
    allow_parallel = True,
    auto_init = False,
    cmd='bb format',
    trigger_mode = TRIGGER_MODE_MANUAL,
    labels = [ 'lint' ],
  )

if use_docs:
  local_resource(
    'docs',
    allow_parallel = True,
    cmd='bb docs',
    deps = [
      'src/babashka',
      'src/main',
    ],
    labels = [ 'compile' ],
  )

if local_devtools:
  local_resource(
    'devtools',
    allow_parallel = True,
    serve_env = {
      'DEVTOOLS_URL': 'http://localhost:9630',
    },
    serve_cmd='bb watch-cljs',
    labels = [ 'dinsro' ],
    links = [
      link('http://localhost:9630', 'Devtools'),
      link('http://localhost:3693', 'Workspaces')
    ],
    readiness_probe = probe(
      http_get = http_get_action(
        port = 9630
      )
    ),
  )

if local_devtools:
  local_resource(
    'rollup',
    allow_parallel = True,
    serve_env = {
      'DEVTOOLS_URL': 'http://localhost:9630',
    },
    serve_cmd='bb install-style-dependencies && bb watch-styles',
    labels = [ 'compile' ],
  )

if use_linting:
  local_resource(
    'eastwood',
    allow_parallel = True,
    auto_init = False,
    cmd = 'bb eastwood',
    deps = [ 'src' ],
    trigger_mode = TRIGGER_MODE_MANUAL,
    labels = [ 'lint' ],
  )

if use_tests:
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

if use_linting:
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

if use_tests:
  local_resource(
    'test-clj',
    allow_parallel = True,
    cmd = 'bb test-clj',
    deps = [ 'src/test' ],
    labels = [ 'test' ],
  )

if use_tests:
  local_resource(
    'test-cljs',
    allow_parallel = True,
    cmd = 'bb test-cljs',
    deps = [ 'src/test' ],
    labels = [ 'test' ],
  )

if use_persistence:
  namespace_create(
    'sqlpad',
    annotations = [ 'field.cattle.io/projectId: local:%s' % project_id ],
    labels = [ 'field.cattle.io/projectId: %s' % project_id ],
  )

if use_persistence:
  k8s_yaml(helm(
    'resources/helm/sqlpad',
    name = 'sqlpad',
    namespace = 'sqlpad',
    set = [
      'image.repository=dinsro/sqlpad',
      'ingress.enabled=true',
      'ingress.hosts[0].host=sqlpad.localhost',
      'ingress.hosts[0].paths[0].path=' + '/',
    ],
  ))

if use_persistence:
  custom_build(
    'dinsro/sqlpad:6.7',
    'earthly --build-arg EXPECTED_REF=$EXPECTED_REF ./resources/tilt/sqlpad+sqlpad',
    [
      'resources/tilt/sqlpad/Earthfile',
      'resources/tilt/sqlpad/seed-data'
    ],
  )

if use_persistence:
  k8s_resource(
    workload = 'sqlpad',
    labels = [ 'database' ],
    links = [
      link('http://sqlpad.localhost', 'SQLPad'),
    ],
  )

k8s_resource(
  workload = 'portal',
  labels = [ 'dinsro' ],
  links = [
    link(portal_url, 'Portal'),
  ],
)

cmd_button(
  'dinsro:format',
  argv = [ 'sh', '-c', 'bb format' ],
  icon_name = 'build_circle',
  resource = 'dinsro',
  text = 'Format',
)

if use_nrepl:
  cmd_button(
    'dinsro:restart',
    argv = [ 'sh', '-c', 'bb restart' ],
    icon_name = 'build_circle',
    resource = 'dinsro',
    text = 'Restart',
  )

  cmd_button(
    'dinsro:seed',
    argv = [ 'sh', '-c', 'bb seed' ],
    icon_name = 'build_circle',
    resource = 'dinsro',
    text = 'Seed',
  )
