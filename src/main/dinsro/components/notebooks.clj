(ns dinsro.components.notebooks
  (:require
   [clojure.string :as string]
   [dinsro.components.config :as config]
   [mount.core :as mount]
   [nextjournal.clerk :as clerk]
   [shadow.cljs.devtools.server.runtime]
   [taoensso.timbre :as log]))

(defn start!
  []
  (when (config/config :use-notebook)
    (log/info "Starting clerk")
    (clerk/serve!
     {:watch-paths    ["notebooks" "src"]
      :show-filter-fn #(string/starts-with? % "notebooks")})
    (clerk/show! "notebooks/notebooks/test.clj")))

(defn stop!
  [_clerk]
  (log/warn "can't stop clerk"))

(mount/defstate ^{:on-reload :noop} notebook-server
  :start (start!)
  :stop (stop! notebook-server))

(comment
  (start!)

  nil)
