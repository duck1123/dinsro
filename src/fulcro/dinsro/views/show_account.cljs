(ns dinsro.views.show-account
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.account-transactions :as u.account-transactions]
   [dinsro.ui.show-account :as u.show-account]
   [taoensso.timbre :as timbre]))

(defsc ShowAccountPage
  [_this {:keys [account-data transactions]}]
  {:initial-state {:account-data {}
                   :transactions {}}
   :route-segment ["show-account"]

   :query [{:account-data (comp/get-query u.show-account/ShowAccount)}
           {:transactions (comp/get-query u.account-transactions/AccountTransactions)}]}
  (dom/section
   :.section
   (dom/div
    :.container
    (dom/div
     :.content
     (dom/div
      :.box
      (dom/h1 (tr [:show-account]))
      (u.show-account/ui-show-account (timbre/spy :info account-data))
      (u.account-transactions/ui-account-transactions transactions))))))
