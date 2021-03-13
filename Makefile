all: init install

init:
	@echo "Hello World"
	# TODO: common init here

await-app:
	while [ "`docker inspect -f {{.State.Health.Status}} app_dinsro_1`" != "healthy" ]; do docker inspect app_dinsro_1 && docker logs app_dinsro_1 && sleep 5; done

build-dev-image:
	earthly -i +dev-image-sources

build-image:
	earthly +build-image

build-production: compile-production package-jar

clean:
	rm -rf resources/main/public/js
	rm -rf resources/workspaces/public/js
	rm -rf resources/dev/public/js
	rm -rf .shadow-cljs/builds
	rm -rf classes/*
	rm -rf target

check:
	clojure -M:cljfmt check src deps.edn shadow-cljs.edn --indents indentation.edn

compile: compile-clj compile-cljs

compile-clj: init
	clojure -M:dev -e "(compile 'dinsro.core)"

compile-cljs: init
	clojure -M:dev:shadow-cljs compile main

compile-production: compile-production-clj compile-production-cljs

compile-production-clj: install
	clojure -M:production -e "(compile 'dinsro.core)"

compile-production-cljs: install
	clojure -M:shadow-cljs release main

dev: build-dev-image start-dev

dev-bootstrap:
	@echo "Bootstrapping dev"
	make run

dev-workspaces-bootstrap: workspaces

display-path:
	clojure -A:cljfmt -Stree
	clojure -A:dev -Stree
	clojure -A:eastwood -Stree
	clojure -A:kibit -Stree
	clojure -A:production -Stree
	clojure -A:shadow-cljs -Stree
	clojure -A:test -Stree
	clojure -A:uberdeps -Stree

e2e:
	earthly -P -i +e2e

format:
	clojure -M:cljfmt fix src deps.edn shadow-cljs.edn --indents indentation.edn

install: init
	npx yarn install --frozen-lockfile

lint: lint-kondo lint-eastwood lint-kibit

lint-eastwood:
	clojure -M:dev:eastwood '{:source-paths ["src/main" "src/test"]}'

lint-kibit:
	clojure -M:dev:kibit --paths src/main,src/test

lint-kondo: install
	npx clj-kondo --parallel --lint "src/main:src/test"

package-jar:
	clojure -M:uberdeps:production --main-class dinsro.core --aliases production

prepare-test-dirs:
	mkdir -p /tmp/dinsro/data/test

test: test-clj test-cljs

test-clj: prepare-test-dirs
	clojure -M:dev:test -d src/test

test-cljs: install
	clojure -M:test:shadow-cljs compile ci
	npx karma start --single-run --check="ci.js"

test-integration:
	npx cypress run

run:
	clojure -M:dev

run-production:
	export DATAHIKE_URL="datahike:file://$(pwd)/data/dev"
	java -jar target/dinsro.jar

server: compile run

start-dev: start-lb
	docker-compose up dinsro watch workspaces

start-lb:
	docker-compose up -d frontend

watch: watch-cljs

watch-cljs: install
	clojure -M:test:shadow-cljs watch main workspaces

workspaces:
	clojure -M:test:shadow-cljs watch workspaces
