(ns dinsro.components.index-currencies
  (:require [dinsro.events.currencies :as e.currencies]
            [dinsro.views.show-currency :as show-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn index-currency-line
  [currency]
  (let [{:keys [db/id dinsro.spec.currencies/name]} currency]
    [:div.box
     #_[:p "Id: " id]
     [:p "Name: " [:a {:href (kf/path-for [:show-currency-page {:id id}])} name]]
     [:a.button.is-danger
      {:on-click #(rf/dispatch [::e.currencies/do-delete-record currency])}
      "Delete"]]))

(defn index-currencies
  [currencies]
  [:<>
   [:p "Index Currencies"]
   #_[:pre (str currencies)]
   (if-not (seq currencies)
     [:div "No Currencies"]
     (into
      [:div.section]
      (for [{:keys [db/id] :as currency} currencies]
        ^{:key id}
        [index-currency-line currency])))])
