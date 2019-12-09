(ns dinsro.views.index-transactions
  (:require [day8.re-frame.tracing :refer-macros [fn-traced]]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.index-transactions :refer [index-transactions]]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn load-buttons
  []
  [:div.box
   #_[c.buttons/fetch-rates]
   #_[c.buttons/fetch-currencies]
   [c.buttons/toggle-debug]])

(defn page
  []
  [:section.section>div.container>div.content
   [load-buttons]
   [:div.box
    [:h1 "Index Transactions"]
    [:hr]
    [index-transactions [{:a "a"}]]]])
