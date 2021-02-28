all: init install

init:
	@echo "Hello World"
	# TODO: common init here

await-app:
	while [ "`docker inspect -f {{.State.Health.Status}} app_fulcro_1`" != "healthy" ]; do docker inspect app_fulcro_1 && docker logs app_fulcro_1 && sleep 5; done

build-dev-image:
	earthly -i +dev-image

build-dev-image-fulcro:
	earthly -i +dev-image-fulcro

build-dev-image-reframe:
	earthly -i +dev-image-sources-reframe

build-image: build-image-fulcro

build-image-fulcro:
	earthly +build-image-fulcro

build-image-reframe:
	earthly +build-image-reframe

build-fulcro-production: compile-fulcro-production package-jar-fulcro

build-production: build-reframe-production build-fulcro-production

build-production-fulcro: build-fulcro-production

build-production-reframe: build-reframe-production

build-reframe-production: compile-reframe-production package-jar-reframe

clean: clean-fulcro clean-outputs clean-reframe

clean-fulcro:
	rm -rf resources/fulcro/public/js
	rm -rf resources/fulcro-workspaces/public/js

clean-outputs:
	rm -rf resources/dev/public/js
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

compile-production-fulcro: compile-fulcro-production

compile-production-reframe: compile-reframe-production

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

dev: build-dev-image start-dev

dev-bootstrap-fulcro: dev-fulcro-bootstrap

dev-bootstrap-reframe: dev-reframe-bootstrap

dev-fulcro: build-dev-image-fulcro start-dev-fulcro

# Entrypoint for duck1123/dinsro:dev-fulcro-latest (+build-dev-image-fulcro)
dev-fulcro-bootstrap:
	@echo "Bootstrapping Fulcro dev"
	make run-fulcro

dev-fulcro-workspaces-bootstrap: workspaces-fulcro

dev-reframe: build-dev-image-reframe start-dev-reframe

dev-reframe-bootstrap: run-reframe

dev-reframe-workspaces-bootstrap: workspaces-reframe

display-path-fulcro:
	clojure -A:cljfmt -Stree
	clojure -A:dev -Stree
	clojure -A:eastwood -Stree
	# clojure -A:fulcro -Stree
	# clojure -A:fulcro-dev -Stree
	clojure -A:kibit -Stree
	clojure -A:production -Stree
	clojure -A:reframe -Stree
	clojure -A:reframe-dev -Stree
	clojure -A:shadow-cljs -Stree
	clojure -A:test -Stree
	clojure -A:uberdeps -Stree

e2e: e2e-fulcro

e2e-fulcro:
	earthly -P -i +e2e-fulcro

format:
	clojure -M:cljfmt fix src deps.edn shadow-cljs.edn --indents indentation.edn

install: init
	npx yarn install --frozen-lockfile

lint: lint-kondo lint-eastwood lint-kibit

lint-eastwood: lint-eastwood-fulcro lint-eastwood-reframe

lint-eastwood-fulcro:
	clojure -M:eastwood:dev:fulcro '{:source-paths ["src/main" "src/fulcro" "src/fulcro-test" "src/test" "env/dev/src"]}'

lint-eastwood-reframe:
	clojure -M:eastwood:dev:reframe '{:source-paths ["src/main" "src/reframe" "src/reframe-test" "src/test" "env/dev/src"]}'

lint-fulcro: lint-kondo-fulcro lint-eastwood-fulcro lint-kibit-fulcro

lint-kibit: lint-kibit-reframe

lint-kibit-fulcro:
	clojure -M:dev:fulcro:kibit --paths src/main,src/test,src/fulcro

lint-kibit-reframe:
	clojure -M:dev:reframe:kibit --paths src/main,src/test,src/reframe

lint-kondo: lint-kondo-fulcro lint-kondo-reframe

lint-kondo-fulcro: install
	npx clj-kondo --parallel --lint "src/main:src/test"

lint-kondo-reframe: install
	npx clj-kondo --parallel --lint "src/main:src/reframe:src/reframe-test:src/test"

lint-reframe: lint-kondo-reframe lint-eastwood-reframe lint-kibit-reframe

package-jar-fulcro:
	clojure -M:uberdeps:production --main-class dinsro.core --aliases fulcro:production

package-jar-reframe:
	clojure -M:uberdeps:production --main-class dinsro.core --aliases reframe:production

prepare-test-dirs:
	mkdir -p /tmp/dinsro/data/test

test: test-fulcro test-reframe

test-clj: test-fulcro-clj test-reframe-clj

test-cljs: test-fulcro-cljs test-reframe-cljs

test-fulcro: test-fulcro-clj test-fulcro-cljs

test-fulcro-clj: prepare-test-dirs
	clojure -M:dev:test:fulcro -d src/test

test-fulcro-cljs: install
	clojure -M:test:fulcro:shadow-cljs compile fulcro-ci
	npx karma start --single-run --check="ci-fulcro.js"

test-integration: test-integration-fulcro

test-integration-fulcro:
	npx cypress run

test-reframe: test-reframe-clj test-reframe-cljs

test-reframe-clj: install prepare-test-dirs
	clojure -M:test:reframe -d src/test -d src/reframe-test

test-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs compile reframe-ci
	npx karma start --single-run --check="ci-reframe.js"

run-fulcro:
	clojure -M:fulcro:dev:fulcro-dev ${CONFIG_PATH:-`pwd`/config3.edn}

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

start-dev: start-lb
	docker-compose up fulcro fulcro-watch fulcro-workspaces reframe reframe-watch reframe-workspaces

start-dev-fulcro: start-lb
	docker-compose up fulcro fulcro-watch fulcro-workspaces

start-dev-reframe: start-lb
	docker-compose up reframe reframe-watch reframe-workspaces

start-lb:
	docker-compose up -d frontend

watch-cljs: watch-fulcro-cljs watch-reframe-cljs

watch-fulcro: watch-fulcro-cljs

watch-fulcro-cljs: install
	clojure -M:test:fulcro:shadow-cljs watch fulcro-main fulcro-workspaces

watch-reframe: watch-reframe-cljs

watch-reframe-cljs: install
	clojure -M:test:reframe:shadow-cljs watch reframe-main

workspaces: workspaces-fulcro workspaces-reframe

workspaces-fulcro:
	clojure -M:test:fulcro:shadow-cljs watch fulcro-workspaces

workspaces-reframe:
	ls -al /home
	sleep 5
	clojure -M:test:reframe:shadow-cljs watch reframe-workspaces
