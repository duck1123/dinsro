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

base_url         = config_get('baseUrl')
project_id       = config_get('projectId')
repo             = config_get('repo')
version          = config_get('version')
devcards         = config_get('devcards')
notebooks        = config_get('notebooks')
docs_enabled     = config_get('docs-enabled')
local_devtools   = config_get('localDevtools')
notebooks_host   = notebooks.get('host')
portal_enabled   = config_get('portal-enabled')
use_linting      = config_get('useLinting')
use_notebooks    = notebooks.get('enabled')
use_nrepl        = config_get('useNrepl')
use_persistence  = config_get('usePersistence')
use_production   = config_get('useProduction')
use_tests        = config_get('useTests')
use_docs         = config_get('useDocs')
use_devcards     = devcards.get('enabled')
devcards_enabled = config_get('devcards-enabled')
portal_url       = 'http://' + config_get('portalHost')
devcards_url     = 'http://devcards.dinsro.localhost'
devcards_devtools_url     = 'http://devtools.devcards.dinsro.localhost'

def get_notebooks_host():
  return "notebooks." + base_url if notebooks.get('inheritHost') else notebooks_host

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

dinsro_dev_image = "%s/dinsro:dev-sources-%s" % (repo, version)
devtools_host = ("devtools.%s" % base_url) if not local_devtools else "localhost:9630"

local_resource(
  'dinsro-values',
  allow_parallel = True,
  cmd='bb generate-dinsro-values',
  deps = [
    'site.edn',
    'src/shared',
    'src/babashka',
  ],
  labels = [ 'compile' ],
)

k8s_yaml(helm(
  'resources/helm/dinsro',
  name = 'dinsro',
  namespace = 'dinsro',
  values=["./target/dinsro_values.yaml"],
))

if use_production:
  earthly_build(
    "%s/dinsro:%s" % (repo, version),
    '+image',
    deps = [
      'Earthfile',
      '.dockerignore',
      'bb.edn',
      'deps.edn',
      'src'
    ],
    build_args = {
      'REPO': repo,
    },
  )

if portal_enabled:
  earthly_build(
    "%s/portal:%s" % (repo, version),
    '+portal',
    deps = [
      'Earthfile',
      '.dockerignore',
      'bb.edn',
      "resources/portal",
      'deps.edn',
    ],
    build_args = {
      'REPO': repo,
    }
  )

if devcards_enabled:
  earthly_build(
    "%s/dinsro:devcards-%s" % (repo, version),
    '+devcards-image',
    deps = [
      'Earthfile',
      '.dockerignore',
      'bb.edn',
      "resources/devcards",
      'src',
      'deps.edn',
      'site.edn',
      'site-defaults.edn',
    ],
    build_args = {
      'REPO': repo,
    },
    live_update=[
      sync('resources/devcards', '/usr/src/app/resources/devcards'),
      sync('site.edn', '/usr/src/app/site.edn'),
      sync('src', '/usr/src/app/src'),
      # sync('tilt_config.json', '/usr/src/app/tilt_config.json'),
    ],
  )

if docs_enabled:
  earthly_build(
    "%s/dinsro:docs-%s" % (repo, version),
    '+docs-image',
    deps = [
      'Earthfile',
      '.dockerignore',
      'bb.edn',
      'deps.edn',
      'site.edn',
      'site-defaults.edn',
      'src',
      'target/doc',
    ],
    build_args = {
      'REPO': repo,
    },
    live_update=[
      sync('site.edn', '/usr/src/app/site.edn'),
      sync('src', '/usr/src/app/src'),
    ],
  )

if not use_production:
  earthly_build(
    dinsro_dev_image,
    '+dev-image-sources',
    build_args = {
      'repo': repo,
      'use_notebooks': ('true' if use_notebooks else 'false'),
      'watch_sources': ('false' if local_devtools else 'true'),
    },
    deps =  [
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
    link(devcards.get('host') or "devcards.dinsro.localhost", 'Cards') if devcards_enabled else None,
    link(get_notebooks_host(), 'Notebooks') if use_notebooks else None,
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

if docs_enabled:
  local_resource(
    'compile-docs',
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

# if use_tests:
#   local_resource(
#     'test-cljs',
#     allow_parallel = True,
#     cmd = 'bb test-cljs',
#     deps = [ 'src/test' ],
#     labels = [ 'test' ],
#   )

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
  earthly_build(
    'dinsro/sqlpad:6.7',
    './resources/tilt/sqlpad+sqlpad',
    deps = [
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

if portal_enabled:
  k8s_resource(
    workload = 'portal',
    labels = [ 'dinsro' ],
    links = [
      link(portal_url, 'Portal'),
    ],
  )

docs_url = 'docs.dinsro.localhost'

if docs_enabled:
  k8s_resource(
    workload = 'docs',
    labels = [ 'dinsro' ],
    links = [
      link(docs_url, 'docs'),
    ],
  )

if devcards_enabled:
  k8s_resource(
    workload = 'devcards',
    labels = [ 'dinsro' ],
    links = [
      link(devcards_url, 'devcards'),
      link(devcards_devtools_url, 'devcards devtools'),
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
