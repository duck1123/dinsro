(ns dinsro.events.debug
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.store :as st]
   [taoensso.timbre :as timbre]))

(s/def ::shown? boolean?)

(s/def ::enabled? boolean?)

(defn toggle-shown?
  [{:keys [db]} _]
  (let [shown? (::shown? db)]
    {:dispatch [::set-shown? (not shown?)]}))

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::shown?)
    (st/reg-set-event ::shown?)
    (st/reg-basic-sub ::enabled?)
    (st/reg-set-event ::enabled?)
    (st/reg-event-fx ::toggle-shown? toggle-shown?))
  store)
