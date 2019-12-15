(ns dinsro.components.debug
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.events.debug :as e.debug]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(defn debug-box
  [data]
  (when @(rf/subscribe [::e.debug/shown?])
    [:pre (str data)]))
