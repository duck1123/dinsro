(ns dinsro.components.currency-accounts
  (:require
   [dinsro.components.index-accounts :as c.index-accounts]))

(defn section
  [store accounts]
  [:div.box
   [:h2 "Accounts"]
   [c.index-accounts/section store accounts]])
