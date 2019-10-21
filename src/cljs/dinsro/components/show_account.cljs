(ns dinsro.components.show-account)

(defn show-account
  [account]
  [:div
   [:p "Account: " (str account)]
   [:p "Name: " (:name account)]])
