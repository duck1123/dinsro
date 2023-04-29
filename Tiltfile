# -*- mode: python -*-
# Tilt

load('ext://local_output', 'local_output')
load('ext://uibutton', 'cmd_button')

config_data = decode_json(local_output('bb tilt-config'))

def config_get(key):
  v = config_data.get(key)
  if (not v == None):
    return v
  else:
    return config_data[key]

base_url              = config_get('baseUrl')
project_id            = config_get('projectId')
repo                  = config_get('repo')
version               = config_get('version')
docs_enabled          = config_get('docs-enabled?')
local_devtools        = config_get('localDevtools')
use_linting           = config_get('useLinting')
use_nrepl             = config_get('useNrepl')
use_persistence       = config_get('usePersistence')
use_production        = config_get('useProduction')
use_sqlpad            = config_get('useSqlpad')
use_tests             = config_get('useTests')
use_docs              = config_get('useDocs')

# Local Resources

local_resource(
  'dinsro',
  serve_cmd='bb run',
  labels = [ 'dinsro' ],
  readiness_probe = probe(
    http_get = http_get_action(
      port = 3000
    )
  ),
)

local_resource(
  'check',
  allow_parallel = True,
  cmd='bb check',
  deps = [ 'src' ],
  labels = [ 'lint' ],
)

local_resource(
  'format',
  allow_parallel = True,
  auto_init = False,
  cmd = 'bb format',
  labels = [ 'lint' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
)

local_resource(
  'test-clj',
  allow_parallel = True,
  auto_init = False,
  cmd = 'bb test-clj',
  labels = [ 'test' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
)

local_resource(
  'test-cljs',
  allow_parallel = True,
  auto_init = False,
  cmd = 'bb test-cljs',
  labels = [ 'test' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
)

local_resource(
  'compile-docs',
  allow_parallel = True,
  auto_init = False,
  cmd='bb docs',
  deps = [
    'src/babashka',
    'src/main',
  ],
  labels = [ 'compile' ],
  trigger_mode = TRIGGER_MODE_MANUAL,
)

local_resource(
  'devtools',
  allow_parallel = True,
  # serve_env = {
  #   'DEVTOOLS_URL': 'http://localhost:9630',
  # },
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

local_resource(
  'rollup',
  allow_parallel = True,
  serve_env = {
    'DEVTOOLS_URL': 'http://localhost:9630',
  },
  serve_cmd='bb install-style-dependencies && bb watch-styles',
  labels = [ 'compile' ],
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
  'funnel',
  allow_parallel = True,
  serve_cmd='bb funnel',
  labels = [ 'compile' ],
)

# if use_tests:
#   local_resource(
#     'test-clj',
#     allow_parallel = True,
#     cmd = 'bb test-clj',
#     deps = [ 'src/test' ],
#     labels = [ 'test' ],
#   )

# if use_tests:
#   local_resource(
#     'test-cljs',
#     allow_parallel = True,
#     cmd = 'bb test-cljs',
#     deps = [ 'src/test' ],
#     labels = [ 'test' ],
#   )

# Buttons

cmd_button(
  'dinsro:format',
  argv = [ 'sh', '-c', 'bb format' ],
  icon_name = 'build_circle',
  resource = 'dinsro',
  text = 'Format',
)

cmd_button(
  'dinsro:yarn',
  argv = [ 'sh', '-c', 'yarn install' ],
  icon_name = 'build_circle',
  resource = 'dinsro',
  text = 'Yarn',
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
