(ns dinsro.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [dinsro.components.config :as c.config]
   [dinsro.components.notebooks]
   [dinsro.components.nrepl]
   [dinsro.components.portal]
   [dinsro.components.seed]
   [dinsro.components.server]
   [lambdaisland.glogc :as log]
   [mount.core :as mount])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ thread ex]
     (log/error {:what      :uncaught-exception
                 :exception ex
                 :where     (str "Uncaught exception on" (.getName thread))} ex))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info :component/stopped {:component component}))
  (shutdown-agents))

(defn start-app [args]
  (log/info :app/starting {:args args})
  (mount/start #'c.config/config)
  (let [options (parse-opts args cli-options)]
    (doseq [component (-> options mount/start-with-args :started)]
      (log/debug :component/started {:component component})))
  (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app)))

(defn -main
  [& args]
  (start-app args))
