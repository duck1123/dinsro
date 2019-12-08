(ns dinsro.components.debug
  (:require
   [clojure.spec.alpha :as s]
   [clojure.spec.gen.alpha :as gen]
   [dinsro.translations :refer [tr]]
   [re-frame.core :as rf]
   [reframe-utils.core :as rfu]

   [taoensso.timbre :as timbre]
   )
  )

(s/def ::debug-shown? boolean?)
(rfu/reg-basic-sub ::debug-shown?)

(defn toggle-debug-button
  []
  (let [debug-shown? @(rf/subscribe [::debug-shown?])]
    [:a.button
     (tr [:debug-shown "Debug Shown: %1"] [(str (boolean debug-shown?))])]))

(defn debug-box
  [data]
  (when @(rf/subscribe [::debug-shown?])
    [:pre (str data)]))
