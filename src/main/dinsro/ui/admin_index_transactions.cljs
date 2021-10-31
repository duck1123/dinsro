(ns dinsro.ui.admin-index-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as log]))

(defsc AdminIndexTransactions
  [_this {::keys [transactions]}]
  {:ident             (fn [_] [:component/id ::AdminIndexTransactions])
   :initial-state     {::transactions  {}}
   :query             [{::transactions (comp/get-query u.index-transactions/IndexTransactions)}]}
  (bulma/box
   (dom/h2 :.title.is-2
     (tr [:transactions]))
   (dom/hr)
   (if (seq transactions)
     (dom/div {}
       (u.index-transactions/ui-index-transactions transactions))
     (dom/p "No data"))))

(def ui-section (comp/factory AdminIndexTransactions))
