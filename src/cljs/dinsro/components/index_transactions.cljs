(ns dinsro.components.index-transactions
  (:require [clojure.spec.alpha :as s]
            ;; [dinsro.events.currencies :as e.currencies]
            ;; [dinsro.events.rates :as e.rates]
            ;; [dinsro.spec.currencies :as s.currencies]
            ;; [dinsro.spec.rates :as s.rates]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [dinsro.translations :refer [tr]]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [reagent.core :as r]
            [re-frame.core :as rf]))

(defn index-transactions
  [items]
  [:div "Index transactions"]
  (if-not (seq items)
    [:p "no items"]
    [:<>
     [:pre (str items)]
     [:table
      [:thead
       [:tr
        [:th "Id"]
        [:th "Value"]
        [:th "Currency"]
        [:th "Account"]
        [:th ""]]]
      (into
       [:tbody]
       (for [transaction items]
         ^{:key (:db/id transaction)}
         [:tr
          [:td (:db/id transaction)]
          [:td (::s.transactions/value transaction)]
          [:td (:db/id (::s.transactions/currency transaction))]
          [:td (:db/id (::s.transactions/account transaction))]
          [:td [:a.button "Click"]]]))]]))
