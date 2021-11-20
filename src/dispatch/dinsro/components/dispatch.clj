(ns dinsro.components.dispatch
  (:require
   [nrepl.core :as nrepl]
   [nrepl.cmdline :as cmdline]
   [nrepl.misc :refer [noisy-future]]
   [nrepl.transport :as transport]
   [taoensso.timbre :as log]))

(defn repl-fn
  [host port cmd]
  (let [transport (nrepl/connect :host host :port port :transport-fn #'transport/bencode)
        client    (nrepl/client transport Long/MAX_VALUE)]
    (noisy-future
     (->> (client)
          (take-while #(nil? (:id %)))
          (run! #(when-let [msg (:out %)] (print msg)))))
    (let [session (nrepl/client-session client)]
      (doseq [res (nrepl/message session {:op "eval" :code cmd})]
        (comment
          (when-let [out (:out res)]
            (println out)))))))

(defn -main
  []
  (repl-fn "127.0.0.1" 7000 "(restart)")
  (println "done")
  (System/exit 0))
