(ns dinsro.components.notebooks
  (:require
   [clojure.string :as string]
   [dinsro.components.config :as config]
   [mount.core :as mount]
   [nextjournal.clerk :as clerk]
   [taoensso.timbre :as log]))

(def initial-page "src/notebooks/dinsro/notebook.clj")

(defn start!
  []
  (when (get-in config/config [::config :enabled])
    (log/info "Starting clerk")
    (clerk/serve!
     {:watch-paths    ["src/main" "src/notebooks" "src/shared"]
      :show-filter-fn #(string/starts-with? % "dinsro.notebook")})
    (clerk/show! initial-page)))

(defn stop!
  [_clerk]
  (log/warn "can't stop clerk"))

(mount/defstate ^{:on-reload :noop} notebook-server
  :start (start!)
  :stop (stop! notebook-server))

(comment
  (start!)

  (clerk/show! initial-page)

  nil)
