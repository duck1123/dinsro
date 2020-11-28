all: init install

init:
	echo "Hello World"

build-production: build-reframe-production

build-reframe-production: compile-reframe-production
	clojure -M:uberdeps --main-class dinsro.core --aliases reframe

clean: clean-fulcro clean-outputs clean-reframe

clean-fulcro:
	rm -rf resources/fulcro/public/js
	rm -rf resources/fulcro-cards/public/js
	rm -rf resources/fulcro-workspaces/public/js

clean-outputs:
	rm -rf .shadow-cljs/builds
	rm -rf classes/*
	rm -rf target

clean-reframe:
	rm -rf resources/reframe/public/js
	rm -rf resources/reframe-cards/public/js
	rm -rf resources/reframe-workspaces/public/js

compile: compile-fulcro compile-reframe

compile-fulcro: compile-fulcro-clj compile-fulcro-cljs

compile-fulcro-clj: init
	clojure -M:fulcro:dev:datomic -e "(compile 'dinsro.core)"

compile-fulcro-cljs: init
	clojure -M:dev:fulcro:datomic:shadow-cljs compile fulcro-main

compile-fulcro-production: compile-fulcro-production-clj compile-production-cljs

compile-fulcro-production-clj: install
	clojure -M:fulcro:datomic:production -e "(compile 'dinsro.core)"

compile-fulcro-production-cljs: install
	clojure -M:shadow-cljs:fulcro:datomic release fulcro-main

compile-production: compile-fulcro-production compile-reframe-production

compile-production-clj: compile-fulcro-production-clj compile-reframe-production-clj

compile-production-cljs: compile-fulcro-production-cljs compile-reframe-production-cljs

compile-production: compile-reframe-production

compile-production-clj: compile-reframe-production-clj

compile-production-cljs: compile-reframe-production-cljs

compile-reframe: compile-reframe-clj compile-reframe-cljs

compile-reframe-clj: init
	# clojure -M:reframe:dev:datomic -e "(compile 'dinsro.core)"

compile-reframe-cljs: init
	clojure -M:dev:reframe:shadow-cljs:datomic compile reframe-main

compile-reframe-production: compile-reframe-production-clj compile-reframe-production-cljs

compile-reframe-production-clj: init
	clojure -M:reframe:datomic:production -e "(compile 'dinsro.core)"

compile-reframe-production-cljs: init
	clojure -M:shadow-cljs:reframe:datomic release reframe-main

dev-fulcro:
	docker-compose up -d fulcro

dev-fulcro-bootstrap:
	make run-fulcro

check:
	clojure -M:cljfmt check src env deps.edn shadow-cljs.edn --indents indentation.edn

dev-reframe-bootstrap:
	make run-reframe

dev-reframe-workspaces-bootstrap:
	make workspaces-reframe

devcards: devcards-fulcro devcards-reframe

devcards-fulcro:
	clojure -M:test:dev:fulcro:fulcro-devcards:devcards:shadow-cljs:datomic watch fulcro-devcards

devcards-reframe:
	clojure -M:test:dev:reframe:reframe-devcards:devcards:shadow-cljs:datomic watch reframe-devcards

format:
	clojure -M:cljfmt fix src env deps.edn shadow-cljs.edn --indents indentation.edn

install: init
	yarn install

lint: lint-kondo lint-eastwood lint-kibit

lint-eastwood:
	clojure -M:eastwood:dev:reframe:datomic '{:source-paths ["src/main" "src/reframe" "src/reframe-test" "src/test" "env/dev/src"]}'

lint-kibit: lint-kibit-reframe

lint-kibit-reframe:
	clojure -M:kibit:dev:reframe:reframe-devcards:datomic --paths src/main,src/test,src/reframe

lint-kondo: lint-kondo-fulcro lint-kondo-reframe

lint-kondo-fulcro:
	npx clj-kondo --parallel --lint "src/main:src/fulcro:src/fulcro-test:src/fulcro-workspaces:src/test"
	npx clj-kondo --parallel --lint "src/main:src/fulcro:src/fulcro-cards:src/fulcro-test:src/test"

lint-kondo-reframe:
	npx clj-kondo --parallel --lint "src/main:src/reframe:src/reframe-test:src/reframe-workspaces:src/test"
	npx clj-kondo --parallel --lint "src/main:src/reframe:src/reframe-cards:src/reframe-test:src/test"

test: test-fulcro test-reframe

test-clj: test-fulcro-clj test-reframe-clj

test-cljs: test-fulcro-cljs test-reframe-cljs

test-fulcro: test-fulcro-clj test-fulcro-cljs

test-fulcro-clj:
	clojure -M:dev:test:datomic:fulcro -d src/test -d src/fulcro-test

test-fulcro-cljs:
	clojure -M:test:fulcro:shadow-cljs:workspaces:fulcro-workspaces:datomic compile fulcro-ci
	npx karma start --single-run --check="ci-fulcro.js"

test-reframe: test-reframe-clj test-reframe-cljs

test-reframe-clj: install
	clojure -M:test:reframe:datomic -d src/test -d src/reframe-test

test-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs:devcards:reframe-devcards:datomic compile reframe-ci
	npx karma start --single-run --check="ci-reframe.js"

run-fulcro:
	clojure -M:fulcro:dev:datomic:fulcro-dev

run-production: run-reframe-production

run-reframe:
	clojure -M:reframe:dev:datomic:reframe-dev

run-reframe-production:
	export DATAHIKE_URL="datahike:file://$(pwd)/data/dev"
	java -jar target/dinsro.jar

server: server-reframe

server-fulcro: compile-fulcro run-fulcro

server-production: server-reframe-production

server-reframe: compile-reframe run-reframe

server-reframe-production: build-reframe-production run-reframe-production

watch-cljs: watch-fulcro-cljs watch-reframe-cljs

watch-fulcro-cljs: install
	clojure -M:test:fulcro:datomic:shadow-cljs:fulcro-workspaces:workspaces watch fulcro-main fulcro-workspaces

watch-reframe: watch-reframe-cljs

watch-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs:datomic watch reframe-main

workspaces: workspaces-fulcro workspaces-reframe

workspaces-fulcro:
	clojure -M:fulcro-workspaces:test:datomic:fulcro:workspaces:shadow-cljs watch fulcro-workspaces

workspaces-reframe:
	clojure -M:reframe-workspaces:test:datomic:reframe:workspaces:shadow-cljs watch reframe-workspaces
