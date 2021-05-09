(ns user
  (:require
   [shadow.cljs.devtools.api :as shadow]
   ;; [shadow.cljs.devtools.server :as server]
   [shadow.cljs.devtools.server.runtime]))

(println "loading user")

(defmacro jit [sym]
  `(requiring-resolve '~sym))

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
