(ns dinsro.components.debug
  (:require
   [cljs.pprint :as p]
   [dinsro.events.debug :as e.debug]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]
   [taoensso.timbre :as timbre]))

(defn hide
  [data]
  (when @(rf/subscribe [::e.debug/shown?]) data))

(defn debug-box
  [data]
  (hide
   [:pre {:style {:max-height "200px"}}
    (with-out-str (p/pprint data))]))
