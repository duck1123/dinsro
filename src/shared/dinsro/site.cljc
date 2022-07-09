(ns dinsro.site
  (:require
   #?(:clj [clojure.edn :as edn])
   #?(:clj [clojure.java.io :as io])
   #?(:cljs [cljs.reader :as reader])
   #?(:cljs ["fs" :as fs])))

;; #?(:cljs (def fs (js/require "fs")))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  #?(:clj
     (try
       (with-open [r (io/reader source)]
         (edn/read (java.io.PushbackReader. r)))
       (catch java.io.IOException _e
         #_(printf "Couldn't open '%s': %s\n" source (.getMessage e))
         nil)
       (catch RuntimeException _e
         #_(printf "Error parsing edn file '%s': %s\n" source (.getMessage e))
         nil))
     :cljs
     (.readFile fs source "utf8"
                (fn [_err data]
                  (let [response (reader/read-string data)]
                    (println response)
                    response)))))

(defn get-site-config
  []
  (let [defaults  (load-edn "site-defaults.edn")
        overrides (load-edn "site.edn")
        notebooks (merge (:notebooks defaults)
                         (:notebooks overrides))]
    (merge defaults overrides {:notebooks notebooks})))
