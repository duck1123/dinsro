(ns dinsro.components.index-accounts
  (:require [dinsro.events.accounts :as e.accounts]
            [kee-frame.core :as kf]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [taoensso.timbre :as timbre]))

(defn index-accounts
  [accounts]
  [:div
   [:p "Index accounts"]
   (if-not (seq accounts)
     [:div "No Accounts"]
     (into
      [:div.section]
      (for [{:keys [id name] :as account} accounts]
        ^{:key (:id account)}
        [:div.column
         {:style {:border        "1px black solid"
                  :margin-bottom "15px"}}
         [:p id]
         [:p name]
         [:a.button {:on-click #(rf/dispatch [::e.accounts/do-delete-account id])} "Delete"]])))])
