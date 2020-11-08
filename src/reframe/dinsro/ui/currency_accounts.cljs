(ns dinsro.ui.currency-accounts
  (:require
   [dinsro.ui.index-accounts :as u.index-accounts]))

(defn section
  [store accounts]
  [:div.box
   [:h2 "Accounts"]
   [u.index-accounts/section store accounts]])
