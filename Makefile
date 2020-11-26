all: init install

init:
	echo "Hello World"

build-production: build-reframe-production

build-reframe-production: compile-reframe-production
	clojure -M:uberdeps --main-class dinsro.core --aliases reframe

clean: clean-reframe clean-outputs

clean-outputs:
	rm -rf .shadow-cljs/builds
	rm -rf classes/*
	rm -rf target

clean-reframe:
	rm -rf resources/reframe/public/js
	rm -rf resources/reframe-cards/public/js
	rm -rf resources/reframe-workspaces/public/js

compile: compile-reframe

compile-production: compile-reframe-production

compile-production-clj: compile-reframe-production-clj

compile-production-cljs: compile-reframe-production-cljs

compile-reframe: compile-reframe-clj compile-reframe-cljs

compile-reframe-clj: init
	clojure -M:reframe:dev -e "(compile 'dinsro.core)"

compile-reframe-cljs: init
	clojure -M:dev:reframe:shadow-cljs compile reframe-main

compile-reframe-production: compile-reframe-production-clj compile-reframe-production-cljs

compile-reframe-production-clj: init
	clojure -M:reframe:production -e "(compile 'dinsro.core)"

compile-reframe-production-cljs: init
	clojure -M:shadow-cljs:reframe release reframe-main

check:
	clojure -M:cljfmt check src env deps.edn shadow-cljs.edn

dev-reframe-bootstrap:
	make run-reframe

dev-reframe-workspaces-bootstrap:
	make workspaces-reframe

devcards: devcards-reframe

devcards-reframe:
	clojure -M:test:dev:reframe:reframe-devcards:devcards:shadow-cljs watch reframe-devcards

format:
	clojure -M:cljfmt fix src env deps.edn shadow-cljs.edn --indents indentation.edn

install: init
	yarn install

lint: lint-kondo lint-eastwood lint-kibit

lint-eastwood:
	clojure -M:eastwood:dev:reframe '{:source-paths ["src/main" "src/reframe" "src/reframe-test" "src/test" "env/dev/src"]}'

lint-kibit: lint-kibit-reframe

lint-kibit-reframe:
	clojure -M:kibit:dev:reframe:reframe-devcards --paths src/main,src/test,src/reframe

lint-kondo: lint-kondo-reframe

lint-kondo-reframe:
	npx clj-kondo --parallel --lint "src/main:src/reframe:src/reframe-test:src/reframe-workspaces:src/test"
	npx clj-kondo --parallel --lint "src/main:src/reframe:src/reframe-cards:src/reframe-test:src/test"

test: test-reframe

test-clj: test-reframe-clj

test-cljs: test-reframe-cljs

test-reframe: test-reframe-clj test-reframe-cljs

test-reframe-clj: install
	clojure -M:test:reframe -d src/test -d src/reframe-test

test-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs:devcards:reframe-devcards compile reframe-ci
	npx karma start --single-run --check="ci-reframe.js"

run-production: run-reframe-production

run-reframe:
	clojure -M:reframe:dev:reframe-dev

run-reframe-production:
	export DATAHIKE_URL="datahike:file://$(pwd)/data/dev"
	java -jar target/dinsro.jar

server: server-reframe

server-production: server-reframe-production

server-reframe: compile-reframe run-reframe

server-reframe-production: build-reframe-production run-reframe-production

watch-cljs: watch-reframe-cljs

watch-reframe: watch-reframe-cljs

watch-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs watch reframe-main

workspaces: workspaces-reframe

workspaces-reframe:
	clojure -M:reframe-workspaces:test:reframe:workspaces:shadow-cljs watch reframe-workspaces
