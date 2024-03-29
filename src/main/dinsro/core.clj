(ns dinsro.core
  (:require
   [clojure.tools.cli :refer [parse-opts]]
   [dinsro.actions.instances :as a.instances]
   [dinsro.components.config :as c.config]
   [dinsro.components.database-queries]
   [dinsro.components.nrepl]
   [dinsro.components.seed :as c.seed]
   [dinsro.components.server]
   [dinsro.components.xtdb]
   [dinsro.ui]
   [lambdaisland.glogc :as log]
   [mount.core :as mount])
  (:gen-class))

;; log uncaught exceptions in threads
(Thread/setDefaultUncaughtExceptionHandler
 (reify Thread$UncaughtExceptionHandler
   (uncaughtException [_ _thread ex]
     ;; (log/error {:what      :uncaught-exception
     ;;             :exception ex
     ;;             :where     (str "Uncaught exception on" (.getName thread))} ex)
     (println ex))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :parse-fn #(Integer/parseInt %)]])

(defn stop-app []
  (doseq [component (:stopped (mount/stop))]
    (log/info :component/stopped {:component component}))
  (shutdown-agents))

(defn start-app [args]
  (log/info :app/starting {:args args})
  (mount/in-cljc-mode)
  (mount/start #'c.config/config-map)
  (let [config (c.config/get-config)]
    (log/trace :start-app/config-loaded {:config config})
    (let [modules (config ::modules)]
      (doseq [module modules]
        (log/info :start-app/requiring {:module module})
        (require (symbol module))))
    (let [options (parse-opts args cli-options)]
      (doseq [component (-> options mount/start-with-args :started)]
        (log/debug :component/started {:component component})))
    (.addShutdownHook (Runtime/getRuntime) (Thread. stop-app))
    (a.instances/register!)))

(defn -main
  [& args]
  (start-app args)
  (c.seed/seed!))
