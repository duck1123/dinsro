(ns dinsro.lib.logging
  "Some helpers to make logging a bit nicer."
  (:require
   ;; IMPORTANT: No explicit require for pprint in cljs. It bloats builds.
   #?@(:clj [[clojure.string :as str]
             [taoensso.encore :as enc]])
   #?(:clj [taoensso.timbre :as log])
   #?(:clj [lambdaisland.glogc :as glog])))

#?(:clj
   (defn custom-output-fn
     "Derived from Timbre's default output function. Used server-side."
     ([data] (custom-output-fn nil data))
     ([opts data]
      ;; (prn data)
      (let [{:keys [no-stacktrace?]}                                 opts
            {:keys [level ?err msg_ ?ns-str ?file timestamp_ context]} data]
        (format
         "%1.1S %s %15s - %s%s%s"
         (name level)
         (force timestamp_)
         (-> (or ?ns-str ?file "?")
             (str/replace-first  "com.fulcrologic." "_")
             (str/replace-first  "dinsro.actions." "_da.")
             (str/replace-first  "dinsro.components." "_dc.")
             (str/replace-first  "dinsro.joins." "_dj.")
             (str/replace-first  "dinsro.model." "_dm.")
             (str/replace-first  "dinsro.mutations." "_dmu.")
             (str/replace-first  "dinsro.queries.core." "_dqc.")
             (str/replace-first  "dinsro.queries." "_dq.")
             (str/replace-first  "dinsro." "_d."))
         (force msg_)
         (if context (str " - " context) "")
         (enc/if-let [_   (not no-stacktrace?)
                      err ?err]
           (str "\n" (log/stacktrace err opts))
           ""))))))

#?(:clj
   (defn configure-logging!
     "Configure clojure logging for this project. `config` is the global config map that should contain
     `:taoensso.timbre/logging-config` as a key."
     [config]
     (let [{::log/keys [logging-config]} config]
       (glog/debug :configure-logging!/starting {:logging-config logging-config})
       (log/merge-config! (assoc logging-config :output-fn custom-output-fn)))))
