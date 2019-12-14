(ns dinsro.components.debug
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.events.debug :as e.debug]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reframe-utils.core :as rfu]
            [taoensso.timbre :as timbre]))

(s/def ::debug-shown? boolean?)
(rfu/reg-basic-sub ::debug-shown?)

(defn toggle-debug
  [{:keys [db]} _]
  {:db (update db ::debug-shown? not)})

(kf/reg-event-fx ::toggle-debug toggle-debug)

(defn toggle-debug-button
  []
  (let [debug-shown? @(rf/subscribe [::debug-shown?])]
    [:a.button
     {:on-click #(rf/dispatch [::toggle-debug])}
     (tr [:debug-shown "Debug Shown: %1"] [(str (boolean debug-shown?))])]))

(defn debug-box
  [data]
  (when (timbre/spy :info @(rf/subscribe [::e.debug/shown?]))
    [:pre (str data)]))
