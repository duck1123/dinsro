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
  (when (get-in config/config [::config :enabled])
    (log/info "Starting clerk")
    (clerk/serve!
     {:watch-paths    ["src/main" "src/notebooks" "src/shared"]
      :show-filter-fn #(string/starts-with? % "dinsro.notebook")})
    (clerk/show! "src/notebooks/dinsro/notebook.clj")))

(defn stop!
  [_clerk]
  (log/warn "can't stop clerk"))

(mount/defstate ^{:on-reload :noop} notebook-server
  :start (start!)
  :stop (stop! notebook-server))

(comment
  (start!)

  (clerk/show! "notebooks/dinsro/notebook.clj")

  nil)
