(ns dinsro.components.logging
  (:require
   [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
   [lambdaisland.glogi :as log]
   [lambdaisland.glogi.console :as glogi-console]
   [taoensso.timbre :as timbre]))

(def root-level :debug)

(def default-levels
  {:glogi/root                                    root-level
   'com.fulcrologic.fulcro.ui-state-machines      :info
   'com.fulcrologic.fulcro.inspect.inspect-client :info
   'dinsro.client                                 :info
   'dinsro.ui.admin                               :info
   'dinsro.ui.breadcrumbs                         :info
   'dinsro.ui.debug                               :info
   #_#_'dinsro.ui.ln                              :debug
   'dinsro.ui.nostr                               :info
   'dinsro.ui.errors                              :info
   'dinsro.ui.links                               :info
   'dinsro.ui.loader                              :info
   'dinsro.ui.menus                               :info
   'dinsro.ui.navbars                             :info
   'goog.net.XhrIo                                :info})

(defn install-logging!
  []
  (glogi-console/install!)

  (log/set-levels default-levels)

  (timbre/merge-config!
   {:level     :debug
    :min-level [["com.fulcrologic.fulcro.inspect.*" :warn]
                ["com.fulcrologic.fulcro.rad.authorization" :warn]
                ["com.fulcrologic.*" :info]
                ["*" :debug]]
    :output-fn prefix-output-fn
    :appenders {:console (console-appender)}})
  (log/debug :logging/initialized {}))
