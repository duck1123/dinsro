(ns dinsro.components.debug
  (:require [dinsro.events.debug :as e.debug]
            [dinsro.translations :refer [tr]]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn hide
  [data]
  (when @(rf/subscribe [::e.debug/shown?]) data))

(defn debug-box
  [data]
  (hide [:pre (str data)]))
