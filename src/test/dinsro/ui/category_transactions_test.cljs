(ns dinsro.ui.category-transactions-test
  (:require
   [dinsro.specs :as ds]
   [dinsro.ui.category-transactions :as u.category-transactions]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]
   [taoensso.timbre :as log]))

(defn get-state
  []
  (ds/gen-key ::u.category-transactions/CategoryTransactions-state))

(ws/defcard CategoryTransactions
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.category-transactions/CategoryTransactions
    ::ct.fulcro3/initial-state get-state}))
