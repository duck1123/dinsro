(ns dinsro.components.index-currencies
  (:require [dinsro.events.currencies :as e.currencies]
            [dinsro.views.show-currency :as show-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn delete-currency-button
  [id]
  [:a.button {:on-click #(rf/dispatch [::e.currencies/do-delete-record id])} "Delete"])

(defn index-currency-line
  [currency]
  (let [{:keys [db/id dinsro.model.currencies/name]} currency]
    [:div.column
     {:style {:border        "1px black solid"
              :margin-bottom "15px"}}
     [:p "Id: " id]
     [:p "Name: " [:a {:href (kf/path-for [:show-currency-page {:id id}])} name]]
     [delete-currency-button id]]))

(defn index-currencies
  [currencies]
  [:div
   [:p "Index Currencies"]
   (if-not (seq currencies)
     [:div "No Currencies"]
     (into
      [:div.section]
      (for [{:keys [db/id] :as currency} currencies]
        ^{:key id}
        [index-currency-line currency])))])
