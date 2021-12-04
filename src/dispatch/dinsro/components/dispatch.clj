(ns dinsro.components.dispatch
  (:require
   [nrepl.core :as nrepl]
   [nrepl.misc :refer [noisy-future]]
   [nrepl.transport :as transport]
   [taoensso.timbre :as log]))

(def repl-host "127.0.0.1")
(def repl-port 7000)

(defn repl-fn
  [host port cmds]
  (let [transport (nrepl/connect :host host :port port :transport-fn #'transport/bencode)
        client    (nrepl/client transport Long/MAX_VALUE)]
    (noisy-future
     (->> (client)
          (take-while #(nil? (:id %)))
          (run! #(when-let [msg (:out %)] (print msg)))))
    (let [session (nrepl/client-session client)]
      (doseq [cmd cmds]
        (doseq [{:keys [out value]
                 :as   res} (nrepl/message session {:op "eval" :code cmd})]
          (cond
            out   (println out)
            value (println value)
            :else (println res)))))))

(defn -main
  [& args]
  (repl-fn repl-host repl-port args)
  (System/exit 0))
