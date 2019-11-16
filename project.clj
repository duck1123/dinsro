(defproject dinsro "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "https://github.com/duck1123/dinsro"

  :dependencies [[buddy "2.0.0"]
                 [cider/cider-nrepl "0.23.0-SNAPSHOT"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [cheshire "5.9.0"]
                 [cider/cider-nrepl "0.23.0-SNAPSHOT"]
                 [cljsjs/highcharts "7.0.3-0"]
                 [clojure.java-time "0.3.2"]
                 [com.cemerick/url "0.1.1"]
                 [com.taoensso/timbre "4.10.0"]
                 [cprop "0.1.14"]
                 [day8.re-frame/http-fx "0.1.6"]
                 [devcards "0.2.6"]
                 [expound "0.7.2"]
                 [funcool/struct "1.4.0"]
                 [io.replikativ/datahike "0.2.0"]
                 [org.eclipse.jetty/jetty-server "9.3.7.v20160115"]
                 [kee-frame "0.3.3" :exclusions [metosin/reitit-core org.clojure/core.async]]
                 [luminus-jetty "0.1.7"]
                 [luminus-transit "0.1.2"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [metosin/muuntaja "0.6.6"]
                 [metosin/reitit "0.3.10"]
                 [metosin/ring-http-response "0.9.1"]
                 [metosin/spec-tools "0.10.0"]
                 [mount "0.1.16"]
                 [nrepl "0.6.0"]
                 [orchestra "2019.02.06-1"]
                 [org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.520" :scope "provided"]
                 [org.clojure/test.check "0.10.0"]
                 [org.clojure/tools.cli "0.4.2"]
                 [org.clojure/tools.logging "0.5.0"]
                 [org.webjars.npm/bulma "0.8.0"]
                 [org.webjars.npm/material-icons "0.3.1"]
                 [org.webjars/webjars-locator "0.37"]
                 [re-frame "0.10.9"]
                 [reagent "0.8.1"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.7.1"]
                 [ring/ring-defaults "0.3.2"]
                 [selmer "1.12.17"]]

  :min-lein-version "2.0.0"

  :source-paths ["src/clj" "src/cljs" "src/cljc"]
  :test-paths ["test/clj"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main ^:skip-aot dinsro.core

  :plugins [[lein-ancient "0.6.15"]
            [lein-cljsbuild "1.1.5"]
            [lein-doo "0.1.11"]
            [lein-kibit "0.1.6"]
            [cider/cider-nrepl "0.23.0-SNAPSHOT"]]

  :clean-targets
  ^{:protect false} [:target-path
                     [:cljsbuild :builds :app :compiler :output-dir]
                     [:cljsbuild :builds :app :compiler :output-to]]

  :figwheel {:http-server-root "public"
             :server-logfile "log/figwheel-logfile.log"
             :nrepl-port 7002
             :css-dirs ["resources/public/css"]
             :nrepl-middleware [cider.piggieback/wrap-cljs-repl
                                cider.nrepl/cider-middleware
                                refactor-nrepl.middleware/wrap-refactor]
             ;; :main dinsro.core
             :ring-handler dinsro.handler/app-routes
             }


  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :dependencies [[day8.re-frame/tracing-stubs "0.5.3"]]
             :cljsbuild
             {:builds {:min {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                             :compiler
                             {:output-dir    "target/cljsbuild/public/js"
                              :output-to     "target/cljsbuild/public/js/app.js"
                              :source-map    "target/cljsbuild/public/js/app.js.map"
                              :optimizations :advanced
                              :infer-externs true
                              :pretty-print  false
                              :closure-warnings
                              {:externs-validation :off :non-standard-jsdoc :off}
                              :externs       ["react/externs/react.js"]}}}}
             :aot :all
             :uberjar-name "dinsro.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev {:jvm-opts ["-Dconf=dev-config.edn"
                            "-Dclojure.spec.check-asserts=true"
                            "-Dlogback.configurationFile=resources/logback.xml"]
                 :dependencies [[binaryage/devtools "0.9.11"]
                                [cider/piggieback "0.4.2"]
                                [day8.re-frame/re-frame-10x "0.4.5"]
                                [day8.re-frame/tracing "0.5.3"]
                                [doo "0.1.11"]
                                [figwheel-sidecar "0.5.19"]
                                [pjstadig/humane-test-output "0.10.0"]
                                [prone "2019-07-08"]
                                [ring/ring-devel "1.8.0"]
                                [ring/ring-mock "0.4.0"]]

                 :plugins      [[lein-doo "0.1.11"]
                                [lein-figwheel "0.5.19"]
                                [org.clojure/tools.namespace "0.3.0-alpha4"
                                 :exclusions [org.clojure/tools.reader]]
                                [refactor-nrepl "2.4.0"
                                 :exclusions [org.clojure/clojure]]]

                 :cljsbuild
                  {:builds
                   {:app      {:source-paths ["src/cljs"
                                              "src/cljc"
                                              "env/dev/cljs"
                                              "test/cljs"
                                              "test/cljc"]
                               :figwheel     {:on-jsload "dinsro.core/mount-components"}
                               :compiler     {:main            "dinsro.app"
                                              :asset-path      "/js/out"
                                              :output-to       "target/cljsbuild/public/js/app.js"
                                              :output-dir      "target/cljsbuild/public/js/out"
                                              :source-map      true
                                              :optimizations   :none
                                              :closure-defines
                                              {"re_frame.trace.trace_enabled_QMARK_"        true
                                               "day8.re_frame.tracing.trace_enabled_QMARK_" true}
                                              :preloads        [day8.re-frame-10x.preload]
                                              :pretty-print    true}}

                    ;; :devcards {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                    ;;            :figwheel     {:devcards true}
                    ;;            :compiler     {:main "dinsro.devcards"
                    ;;                           :asset-path "js/devcards_out"
                    ;;                           :output-to "target/cljsbuild/public/js/app_devcards.js"
                    ;;                           :output-dir "target/cljsbuild/public/js/devcards_out"
                    ;;                           :source-map-timestamp true
                    ;;                           :optimizations :none
                    ;;                           :pretty-print true}}

                    }}

                 :doo {:build "test"
                       :alias {:default [:chrome]}}
                 :source-paths ["env/dev/clj"]
                 :resource-paths ["env/dev/resources"]
                 :repl-options   {:init-ns user}
                 :injections     [(require 'pjstadig.humane-test-output)
                                  (pjstadig.humane-test-output/activate!)]}
   :project/test
   {:jvm-opts ["-Dconf=test-config.edn"]
    :resource-paths ["env/test/resources"]
    :cljsbuild
    {:builds {:test {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to     "target/test.js"
                      :main          "starter.doo"
                      :optimizations :none}}}}}
   :profiles/dev {}
   :profiles/test {}})
