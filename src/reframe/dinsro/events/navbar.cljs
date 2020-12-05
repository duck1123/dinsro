(ns dinsro.events.navbar
  (:require
   [dinsro.store :as st]))

(defn toggle-navbar
  [{:keys [db]} _]
  {:db (update db ::expanded? not)})

(defn nav-link-activated
  [_ _]
  {:dispatch [::toggle-navbar]})

(defn init-handlers!
  [store]
  (doto store
    (st/reg-basic-sub ::expanded?)
    (st/reg-set-event ::expanded?)
    (st/reg-event-fx ::toggle-navbar toggle-navbar)
    (st/reg-event-fx ::nav-link-activated nav-link-activated))
  store)
