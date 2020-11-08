(ns dinsro.ui.debug
  (:require
   [cljs.pprint :as p]
   [dinsro.events.debug :as e.debug]
   [dinsro.store :as st]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(defn hide
  [store data]
  (when @(st/subscribe store [::e.debug/shown?]) data))

(defn debug-box
  [store data]
  (hide store
   [:pre {:style {:max-height "200px"}}
    (with-out-str (p/pprint data))]))
