(ns user
  (:require
   [clojure.tools.namespace.repl :as tools-ns :refer [set-refresh-dirs]]
   [dinsro.components.nrepl :as c.nrepl]
   [lambdaisland.glogc :as log]
   [mount.core :as mount]
   [shadow.cljs.devtools.api :as shadow]
   [shadow.cljs.devtools.server.runtime]))

(set-refresh-dirs "src/main" "src/dev")

(defmacro jit [sym]
  `(requiring-resolve '~sym))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
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

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def go start)

(defn restart
  "Stop, refresh, and restart the server."
  []
  (log/info :restart/starting {})
  (stop)
  (tools-ns/refresh :after 'user/start))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def reset #'restart)
