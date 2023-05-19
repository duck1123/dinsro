(ns dinsro.components.logging
  (:require
   [com.fulcrologic.fulcro.algorithms.timbre-support :refer [console-appender prefix-output-fn]]
   [lambdaisland.glogi :as log]
   [lambdaisland.glogi.console :as glogi-console]
   [taoensso.timbre :as timbre]))

(defn install-logging!
  []
  (glogi-console/install!)

  (log/set-levels
   {:glogi/root                                    :info
    'com.fulcrologic.fulcro.ui-state-machines      :info
    'com.fulcrologic.fulcro.inspect.inspect-client :info
    'dinsro.ui.admin                               :info
    ;; 'dinsro.ui.ln                                  :debug
    'dinsro.ui.nostr                               :info
    'dinsro.ui.errors                              :info
    ;; 'dinsro.ui.links                               :debug
    'dinsro.ui.links                               :info
    ;; 'dinsro.ui.loader                               :debug
    'dinsro.ui.menus                               :info
    'dinsro.ui.navbars                             :info
    'goog.net.XhrIo                                :info})

  (timbre/merge-config!
   {:level     :info
    :min-level :info
    :output-fn prefix-output-fn
    :appenders {:console (console-appender)}})
  (log/debug :logging/initialized {}))
