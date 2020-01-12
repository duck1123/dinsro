(ns dinsro.events.debug
  (:require
   [clojure.spec.alpha :as s]
   [kee-frame.core :as kf]
   [reframe-utils.core :as rfu]
   [taoensso.timbre :as timbre]))

(s/def ::shown? boolean?)
(rfu/reg-basic-sub ::shown?)
(rfu/reg-set-event ::shown?)

(s/def ::enabled? boolean?)
(rfu/reg-basic-sub ::enabled?)
(rfu/reg-set-event ::enabled?)

(defn toggle-shown?
  [{:keys [db]} _]
  (let [shown? (::shown? db)]
    {:dispatch [::set-shown? (not shown?)]}))

(kf/reg-event-fx ::toggle-shown? toggle-shown?)
