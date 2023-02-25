(ns dinsro.site
  (:require
   #?(:clj [clojure.edn :as edn])
   #?(:clj [clojure.java.io :as io])
   #?(:cljs [cljs.reader :as reader])
   #?(:cljs ["fs" :as fs])
   [clojure.spec.alpha :as s]
   [dinsro.site.devcards :as site.devcards]
   [dinsro.site.devtools :as site.devtools]
   [dinsro.site.docs :as site.docs]
   [dinsro.site.nodes :as site.nodes]
   [dinsro.site.notebooks :as site.notebooks]
   [dinsro.site.workspaces :as site.workspaces]))

;; #?(:cljs (def fs (js/require "fs")))

(s/def ::seedDatabase boolean?)
(s/def ::baseUrl string?)
(s/def ::logLevel string?)
(s/def ::useLinting boolean?)
(s/def ::repo string?)
(s/def ::version string?)
(s/def ::projectId string?)
(s/def ::useDocs boolean?)
(s/def ::portalHost string?)
(s/def ::localDevtools boolean?)
(s/def ::useCards boolean?)
(s/def ::seedDatabase boolean?)
(s/def ::useGuardrails boolean?)
(s/def ::useLinting boolean?)
(s/def ::useNrepl boolean?)
(s/def ::usePersistence boolean?)
(s/def ::usePortal boolean?)
(s/def ::useProduction boolean?)
(s/def ::useSqlpad boolean?)
(s/def ::useTests boolean?)

(s/def ::site
  (s/keys
   :opt-un
   [::baseUrl
    ::seedDatabase
    ::logLevel
    ::useLinting
    ::repo
    ::version
    ::projectId
    ::useDocs
    ::portalHost
    ::localDevtools
    ::useCards
    ::seedDatabase
    ::useGuardrails
    ::useLinting
    ::site.devcards/devcards
    ::site.devtools/devtools
    ::site.docs/docs]))

(s/def ::site-defaults
  (s/keys
   :req-un
   [::baseUrl
    ::repo
    ::version
    ::projectId
    ::site.devcards/devcards
    ::site.devtools/devtools
    ::site.docs/docs
    ::logLevel
    ::site.nodes/nodes
    ::site.notebooks/notebooks
    ::site.workspaces/workspaces
    ::useDocs
    ::portalHost
    ::localDevtools
    ::useCards
    ::seedDatabase
    ::useGuardrails
    ::useLinting
    ::useNrepl
    ::usePersistence
    ::usePortal
    ::useProduction
    ::useSqlpad
    ::useTests]))

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
