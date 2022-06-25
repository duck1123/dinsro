(ns dinsro.components.notebooks
  (:require
   [clojure.string :as string]
   [dinsro.components.config :as config]
   [lambdaisland.glogc :as log]
   [mount.core :as mount]
   [nextjournal.clerk :as clerk]))

(def initial-page "src/notebooks/dinsro/notebook.clj")

(defn start!
  []
  (if (get-in config/config [::config :enabled])
    (do
      (log/info :start!/enabled {})
      (clerk/serve!
       {:watch-paths    ["src/main" "src/notebooks" "src/shared"]
        :show-filter-fn #(string/starts-with? % "dinsro.notebooks")})
      (clerk/show! initial-page))
    (log/info :start!/not-enabled {})))

(defn stop!
  [_clerk]
  (log/warn :stop!/cannot-stop {}))

(mount/defstate ^{:on-reload :noop} notebooks-server
  :start (start!)
  :stop (stop! notebooks-server))

(comment
  (start!)

  nil)
