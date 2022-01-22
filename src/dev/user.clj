(ns user
  (:require
   [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]
   [dinsro.components.nrepl :as c.nrepl]
   [dinsro.seed :as seed]
   [mount.core :as mount]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.devtools.server.runtime]
   [taoensso.timbre :as log]))

(set-refresh-dirs "src/main" "src/dev")

(defmacro jit [sym]
  `(requiring-resolve '~sym))

(defn seed!
  []
  (seed/seed-db!))

(defn cljs-repl
  ([]
   (cljs-repl :main))
  ([build-id]
   ;; (server/start!)
   ;; (shadow/watch build-id)
   (loop []
     (println "Trying to connect")
     (when (nil? @@(jit shadow.cljs.devtools.server.runtime/instance-ref))
       (Thread/sleep 1000)
       (recur)))
   ((jit shadow.cljs.devtools.api/nrepl-select) build-id)))

(defn start []
  (mount/start-with-args {:config "config/dev.edn"})
  :ok)

(defn stop
  "Stop the server."
  []
  (mount/stop-except #'c.nrepl/repl-server))

(def go start)

(defn restart
  "Stop, refresh, and restart the server."
  []
  (log/info "Restarting")
  (stop)
  (tools-ns/refresh :after 'user/start))

(def reset #'restart)
