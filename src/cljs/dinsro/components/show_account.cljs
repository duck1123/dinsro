(ns dinsro.components.show-account
  (:require [dinsro.spec.accounts :as s.accounts]
            [orchestra.core :refer [defn-spec]]))

(defn-spec show-account vector?
  [account ::s.accounts/item]
  [:div
   [:p "Account: " (str account)]
   [:p "Name: " (::s.accounts/name account)]
   [:p "User: " (str (::s.accounts/user account))]
   [:p "Currency: " (str (::s.accounts/currency account))]
   [:button.button.is-danger "Delete"]])
