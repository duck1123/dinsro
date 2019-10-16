(ns dinsro.components.index-currencies
  (:require [re-frame.core :as rf]
            [taoensso.timbre :as timbre]))

(defn index-currencies
  [currencies]
  [:div
   [:p "Index Currencies"]
   (if (seq currencies)
     (into
      [:div.section]
      (for [{:keys [id name is-primary exchange] :as currency} currencies]
        ^{:key id}
        [:div.column
         {:style {:border        "1px black solid"
                  :margin-bottom "15px"}}
         [:p "Id: " id]
         [:p "Name: " name]
         (if is-primary
           [:p "Primary"]
           [:p "Exchange " exchange])]))
     [:div "No Currencies"])])
