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
       {:watch-paths ["src/main" "src/notebooks" "src/shared"]
        :show-filter-fn
        (fn [path]
          (log/trace :show-filter-fn/starting {:path path})
          (let [matched (string/starts-with? path "src/notebooks")]
            (log/fine :show-filter-fn/tested {:path path :matched matched})
            matched))})
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
  (stop! notebooks-server)

  nil)
