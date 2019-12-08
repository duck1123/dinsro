(ns dinsro.components.index-currencies
  (:require [dinsro.events.currencies :as e.currencies]
            [dinsro.translations :refer [tr]]
            [dinsro.views.show-currency :as show-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn delete-button
  [currency]
  [:a.button.is-danger
   {:on-click #(rf/dispatch [::e.currencies/do-delete-record currency])}
   (tr [:delete])])

(defn currency-link
  [currency-id]
  [:a {:href (kf/path-for [:show-currency-page {:id id}])} name])

(defn index-currency-line
  [currency]
  (let [{:keys [db/id]} currency]
    [:div.box
     [:p (tr [:name-label] [[currency-link id]])]
     [delete-button]]))

(defn index-currencies
  [currencies]
  [:<>
   #_[:pre (str currencies)]
   (if-not (seq currencies)
     [:div (tr [:no-currencies]) ]
     (into [:div]
           (for [{:keys [db/id] :as currency} currencies]
             ^{:key id}
             [index-currency-line currency])))])
