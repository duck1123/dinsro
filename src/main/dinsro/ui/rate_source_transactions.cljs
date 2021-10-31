(ns dinsro.ui.rate-source-transactions
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.bulma :as bulma]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as log]))

(defsc RateSourceTransactions
  [_this {::keys [transactions]}]
  {:ident             (fn [_] [:component/id ::RateSourceTransactions])
   :initial-state     {::m.rate-sources/id nil
                       ::form              {}
                       ::transactions      {}}
   :query             [::m.rate-sources/id
                       {::transactions (comp/get-query u.index-transactions/IndexTransactions)}]}
  (bulma/box
   (dom/h2 :.title.is-2
     (tr [:transactions]))
   (dom/hr)
   (if (seq transactions)
     (dom/div {}
       (u.index-transactions/ui-index-transactions transactions))
     (dom/p "No data"))))

(def ui-rate-source-transactions (comp/factory RateSourceTransactions))
