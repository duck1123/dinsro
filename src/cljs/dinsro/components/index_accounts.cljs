(ns dinsro.components.index-accounts
  (:require [dinsro.events.accounts :as e.accounts]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn index-accounts
  []
  (let [accounts @(rf/subscribe [::e/accounts])]
    [:div
     [:a.button {:on-click #(rf/dispatch [::e.accounts/do-fetch-accounts])} "Load"]
     [:p "Index accounts"]
     (into
      [:div.section]
      (for [{:keys [id name] :as account} accounts]
        ^{:key (:id account)}
        [:div.column
         {:style {:border "1px black solid"
                  :margin-bottom "15px"}}
         [:p id]
         [:p name]
         [:a.button {:on-click #(rf/dispatch [::e.accounts/do-delete-account id])} "Delete"]]))]))
