(defproject dinsro "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "https://github.com/duck1123/dinsro"

  :dependencies [[buddy "2.0.0"]
                 [camel-snake-kebab "0.4.0"]
                 [cider/cider-nrepl "0.21.1"]
                 [clj-time "0.15.1"]
                 [cljs-ajax "0.8.0"]
                 [com.h2database/h2 "1.4.199"]
                 [com.taoensso/timbre "4.10.0"]
                 [compojure "1.6.1"]
                 [conman "0.8.3"]
                 [cprop "0.1.13"]
                 [crypto-password "0.2.0"]
                 [funcool/struct "1.3.0"]
                 [luminus-immutant "0.2.5"]
                 [luminus-migrations "0.6.4"]
                 [luminus-nrepl "0.1.6"]
                 [luminus/ring-ttl-session "0.3.2"]
                 [markdown-clj "1.0.3"]
                 [metosin/compojure-api "2.0.0-alpha19"]
                 [metosin/muuntaja "0.5.0"]
                 [metosin/ring-http-response "0.9.0"]
                 [metosin/spec-tools "0.7.2"]
                 [mount "0.1.13"]
                 [org.clojure/clojure "1.10.0"]
                 [org.clojure/clojurescript "1.10.339" :scope "provided"]
                 [org.clojure/test.check "0.10.0-alpha2"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/tools.logging "0.4.1"]
                 [org.webjars.bower/tether "1.4.4"]
                 [org.webjars/bootstrap "4.1.3"]
                 [org.webjars/font-awesome "5.3.1"]
                 [reagent "0.8.1"]
                 [re-material-ui-1 "0.1.0-SNAPSHOT"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.7.0"]
                 [ring/ring-defaults "0.3.2"]
                 [secretary "1.2.3"]
                 [selmer "1.12.1"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot dinsro.core

  :plugins [[lein-cljsbuild "1.1.5"]
            [lein-doo "0.1.11"]
            [lein-immutant "2.1.0"]
            [lein-kibit "0.1.2"]]

  :clean-targets
  ^{:protect false} [:target-path
                     [:cljsbuild :builds :app :compiler :output-dir]
                     [:cljsbuild :builds :app :compiler :output-to]]

  :figwheel {:http-server-root "public"
             :nrepl-port 7002
             :css-dirs ["resources/public/css"]
             :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl
                                cider.nrepl/cider-middleware]}

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              [{:id "min"
                 :source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                 :compiler
                 {:output-dir "target/cljsbuild/public/js"
                  :output-to "target/cljsbuild/public/js/app.js"
                  :source-map "target/cljsbuild/public/js/app.js.map"
                  :optimizations :advanced
                  :pretty-print false
                  :closure-warnings
                  {:externs-validation :off :non-standard-jsdoc :off}
                  :externs ["react/externs/react.js"]}}]}

             :aot :all
             :uberjar-name "dinsro.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev {:jvm-opts ["-Dconf=dev-config.edn" "-Dclojure.spec.check-asserts=true"]
                 :dependencies [[binaryage/devtools "0.9.10"]
                                 [cider/piggieback "0.4.0"]
                                 [com.cemerick/piggieback "0.2.2"]
                                 [doo "0.1.11"]
                                 [expound "0.7.2"]
                                 [figwheel-sidecar "0.5.18"]
                                 [nrepl "0.6.0"]
                                 [pjstadig/humane-test-output "0.9.0"]
                                 [prone "1.6.1"]
                                 [ring/ring-devel "1.7.1"]
                                 [ring/ring-mock "0.3.2"]]

                  :plugins      [[com.jakemccrary/lein-test-refresh "0.19.0"]
                                 [lein-doo "0.1.11"]
                                 [lein-figwheel "0.5.18"]
                                 [org.clojure/clojurescript "1.10.520"]]

                  :cljsbuild
                  {:builds
                   [{:id "app"
                     :source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :figwheel {:on-jsload "dinsro.core/mount-components"}
                     :compiler
                     {:main "dinsro.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true}}]}

                  :doo {:build "test"}
                  :source-paths ["env/dev/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user
                                 :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test
   {:jvm-opts ["-Dconf=test-config.edn"]
    :resource-paths ["env/test/resources"]
    :cljsbuild
    {:builds [{:id "test"
               :source-paths ["src/cljc" "src/cljs" "test/cljs"]
               :compiler
               {:output-to "target/test.js"
                :main "dinsro.doo-runner"
                :optimizations :whitespace
                :pretty-print true}}]}}
   :profiles/dev {}
   :profiles/test {}})