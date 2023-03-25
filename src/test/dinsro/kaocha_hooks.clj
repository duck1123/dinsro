(ns dinsro.kaocha-hooks
  (:require
   [clojure.java.browse :as browse]
   [clojure.java.shell :as sh]
   [etaoin.api :as e]
   [kaocha.cljs2.funnel-client :as funnel]
   [shadow.cljs.devtools.api :as shadow-api]
   [shadow.cljs.devtools.server :as shadow-server]
   [shadow.cljs.devtools.server.runtime :as shadow-runtime]))

(defn spawn
  "Start a process, connecting its stdout/stderr to the parent so we see what's
  going on. Returns the Process object so you can call .pid, .destroy,
  .destroyForcibly."
  [args opts]
  ;; (println "spawn")
  (let [builder     (doto (ProcessBuilder. args) (.inheritIO))
        environment (.environment builder)]
    (when-let [env (:env opts)]
      (doseq [[k v] env]
        (.put environment k v)))
    (.start builder)))

(defn ensure-funnel!
  []
  ;; If funnel is already running then this is a no-op
  (sh/sh "bin/funnel_wrapper" "-vv" "--daemonize"))

(defn ensure-shadow-instance!
  []
  (when (nil? @shadow-runtime/instance-ref)
    (shadow-server/start!)
    (loop []
      (Thread/sleep 250)
      (when (nil? @shadow-runtime/instance-ref)
        (recur)))))

(defn shadow-dev-build!
  [testable]
  (shadow-api/compile (:shadow/build testable)))

(defn pre-load
  [testable _config]
  (ensure-funnel!)
  (ensure-shadow-instance!)
  (shadow-dev-build! testable)
  ;; Always return the first argument from Kaocha hooks
  testable)

(def driver nil)

(defn launch-headless-browser-and-wait
  [{:funnel/keys [conn] :kaocha.cljs2/keys [timeout]}]
  (let [host "localhost" port 1818]
    (when (empty? (funnel/list-clients conn))
      (alter-var-root #'driver (constantly (e/firefox {:headless true})))
      (e/go driver (str "http://" host ":" port))))
  (funnel/wait-for-clients conn (if timeout {:timeout timeout} {})))

(defn launch-browser-and-wait
  [{:funnel/keys [conn] :kaocha.cljs2/keys [timeout]}]
  (let [host "localhost" port 1818]
    (when (empty? (funnel/list-clients conn))
      (browse/browse-url (str "http://" host ":" port))))
  (funnel/wait-for-clients conn (if timeout {:timeout timeout} {})))
