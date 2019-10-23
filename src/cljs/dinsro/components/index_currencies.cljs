(ns dinsro.components.index-currencies
  (:require [dinsro.views.show-currency :as show-currency]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn index-currencies
  [currencies]
  [:div
   [:p "Index Currencies"]
   (if-not (seq currencies)
     [:div "No Currencies"]
     (into
      [:div.section]
      (for [{:keys [id name is-primary exchange] :as currency} currencies]
        ^{:key id}
        [:div.column
         {:style {:border        "1px black solid"
                  :margin-bottom "15px"}}
         [:p "Id: " id]
         [:p "Name: " [:a {:href (kf/path-for [:show-currency-page {:id id}])} name]]
         (if is-primary
           [:p "Primary"]
           [:p "Exchange " exchange])])))])
