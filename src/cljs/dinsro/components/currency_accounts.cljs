(ns dinsro.components.currency-accounts
  (:require
   [dinsro.components.index-accounts :as c.index-accounts]))

(defn section
  [accounts]
  [:div.box
   [:h2 "Accounts"]
   [c.index-accounts/index-accounts accounts]])
