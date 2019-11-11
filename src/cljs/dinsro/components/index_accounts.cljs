(ns dinsro.components.index-accounts
  (:require [dinsro.events.accounts :as e.accounts]
            [dinsro.views.show-account :as v.show-account]
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
      (for [{:keys [db/id
                    dinsro.model.account/currency-id
                    dinsro.model.account/name
                    dinsro.model.account/user-id]
             :as account} accounts]
        ^{:key id}
        [:div.column
         {:style {:border        "1px black solid"
                  :margin-bottom "15px"}}
         [:p id]
         [:p [:a {:href (kf/path-for [::v.show-account/page account])} name]]
         [:p "User: " user-id]
         [:p "Currency: " currency-id]
         [:pre (str account)]
         [:a.button {:on-click #(rf/dispatch [::e.accounts/do-delete-account id])} "Delete"]])))])
