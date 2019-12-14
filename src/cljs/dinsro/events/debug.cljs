(ns dinsro.events.debug
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(defn toggle-shown?
  [{:keys [db]} _]
  (let [shown? (::shown? db)]
    {:dispatch [::set-shown? (not shown?)]}))

(kf/reg-event-fx ::toggle-shown? toggle-shown?)
