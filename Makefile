all: init install

init:
	echo "Hello World"

build-production: build-reframe-production build-fulcro-production

build-fulcro-production: compile-fulcro-production
	clojure -M:uberdeps --main-class dinsro.core --aliases fulcro

build-reframe-production: compile-reframe-production
	clojure -M:uberdeps --main-class dinsro.core --aliases reframe

clean: clean-fulcro clean-outputs clean-reframe

clean-fulcro:
	rm -rf resources/fulcro/public/js
	rm -rf resources/fulcro-workspaces/public/js

clean-outputs:
	rm -rf .shadow-cljs/builds
	rm -rf classes/*
	rm -rf target

clean-reframe:
	rm -rf resources/reframe/public/js
	rm -rf resources/reframe-workspaces/public/js

check:
	clojure -M:cljfmt check src deps.edn shadow-cljs.edn --indents indentation.edn

compile: compile-fulcro compile-reframe

compile-fulcro: compile-fulcro-clj compile-fulcro-cljs

compile-fulcro-clj: init
	clojure -M:fulcro:dev -e "(compile 'dinsro.core)"

compile-fulcro-cljs: init
	clojure -M:dev:fulcro:shadow-cljs compile fulcro-main

compile-fulcro-production: compile-fulcro-production-clj compile-fulcro-production-cljs

compile-fulcro-production-clj: install
	clojure -M:fulcro:production -e "(compile 'dinsro.core)"

compile-fulcro-production-cljs: install
	clojure -M:shadow-cljs:fulcro release fulcro-main

compile-production: compile-fulcro-production compile-reframe-production

compile-production-clj: compile-fulcro-production-clj compile-reframe-production-clj

compile-production-cljs: compile-fulcro-production-cljs compile-reframe-production-cljs

compile-reframe: compile-reframe-clj compile-reframe-cljs

compile-reframe-clj: init
	# clojure -M:reframe:dev -e "(compile 'dinsro.core)"

compile-reframe-cljs: init
	clojure -M:dev:reframe:shadow-cljs compile reframe-main

compile-reframe-production: compile-reframe-production-clj compile-reframe-production-cljs

compile-reframe-production-clj: init
	clojure -M:reframe:production -e "(compile 'dinsro.core)"

compile-reframe-production-cljs: init
	clojure -M:shadow-cljs:reframe:production release reframe-main

dev: dev-fulcro

dev-fulcro: start-lb
	docker-compose up fulcro fulcro-watch

dev-fulcro-bootstrap:
	make run-fulcro

dev-reframe: start-lb
	docker-compose up reframe reframe-watch

dev-reframe-bootstrap:
	make run-reframe

dev-reframe-workspaces-bootstrap:
	make workspaces-reframe

format:
	clojure -M:cljfmt fix src deps.edn shadow-cljs.edn --indents indentation.edn

install: init
	npx yarn install

lint: lint-kondo lint-eastwood lint-kibit

lint-eastwood:
	clojure -M:eastwood:dev:reframe '{:source-paths ["src/main" "src/reframe" "src/reframe-test" "src/test" "env/dev/src"]}'

lint-kibit: lint-kibit-reframe

lint-kibit-fulcro:
	clojure -M:kibit:dev:fulcro:fulcro-workspaces --paths src/main,src/test,src/fulcro

lint-kibit-reframe:
	clojure -M:kibit:dev:reframe:reframe-workspaces --paths src/main,src/test,src/reframe

lint-kondo: lint-kondo-fulcro lint-kondo-reframe

lint-kondo-fulcro:
	npx clj-kondo --parallel --lint "src/main:src/fulcro:src/fulcro-test:src/fulcro-workspaces:src/test"

lint-kondo-reframe:
	npx clj-kondo --parallel --lint "src/main:src/reframe:src/reframe-test:src/reframe-workspaces:src/test"

prepare-test-dirs:
	mkdir -p /tmp/dinsro/data/test

test: test-fulcro test-reframe

test-clj: test-fulcro-clj test-reframe-clj

test-cljs: test-fulcro-cljs test-reframe-cljs

test-fulcro: test-fulcro-clj test-fulcro-cljs

test-fulcro-clj: prepare-test-dirs
	clojure -M:dev:test:fulcro -d src/test -d src/fulcro-test

test-fulcro-cljs: install
	clojure -M:test:fulcro:shadow-cljs:workspaces:fulcro-workspaces compile fulcro-ci
	npx karma start --single-run --check="ci-fulcro.js"

test-integration: test-integration-fulcro

test-integration-fulcro:
	npx cypress run

test-reframe: test-reframe-clj test-reframe-cljs

test-reframe-clj: install prepare-test-dirs
	clojure -M:test:reframe -d src/test -d src/reframe-test

test-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs:workspaces:reframe-workspaces compile reframe-ci
	npx karma start --single-run --check="ci-reframe.js"

run-fulcro:
	clojure -A:fulcro:dev:fulcro-dev ${CONFIG_PATH:-`pwd`/config3.edn}

run-production: run-reframe-production

run-reframe:
	clojure -M:reframe:dev:reframe-dev

run-fulcro-production:
	export DATAHIKE_URL="datahike:file://$(pwd)/data/dev"
	java -jar target/dinsro.jar

run-reframe-production:
	export DATAHIKE_URL="datahike:file://$(pwd)/data/dev"
	java -jar target/dinsro.jar

server: server-reframe

server-fulcro: compile-fulcro run-fulcro

server-production: server-reframe-production

server-reframe: compile-reframe run-reframe

server-reframe-production: build-reframe-production run-reframe-production

start-lb:
	docker-compose up -d frontend

watch-cljs: watch-fulcro-cljs watch-reframe-cljs

watch-fulcro: watch-fulcro-cljs

watch-fulcro-cljs: install
	clojure -M:test:fulcro:shadow-cljs:fulcro-workspaces:workspaces watch fulcro-main fulcro-workspaces

watch-reframe: watch-reframe-cljs

watch-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs watch reframe-main

workspaces: workspaces-fulcro workspaces-reframe

workspaces-fulcro:
	clojure -M:fulcro-workspaces:test:fulcro:workspaces:shadow-cljs watch fulcro-workspaces

workspaces-reframe:
	clojure -M:reframe-workspaces:test:reframe:workspaces:shadow-cljs watch reframe-workspaces
