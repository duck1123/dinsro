(ns dinsro.views.show-transaction
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.data-fetch :as df]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.show-transaction :as u.show-transaction]
   [taoensso.timbre :as log]))

(defsc ShowTransactionPage
  [_this {::keys [transaction]}]
  {:ident         (fn [] [:page/id ::page])
   :initial-state {::transaction {}}
   :query         [{::transaction (comp/get-query u.show-transaction/ShowTransactionFull)}]
   :route-segment ["transactions" ::m.transactions/id]
   :will-enter
   (fn [app {::m.accounts/keys [id]}]
     (when-let [uuid (new-uuid id)]
       (df/load app [::m.transactions/id uuid] u.show-transaction/ShowTransactionFull
                {:target [:page/id ::page ::transaction]}))
     (dr/route-immediate (comp/get-ident ShowTransactionPage {})))}
  (if (::m.transactions/id transaction)
    (u.show-transaction/ui-show-transaction-full transaction)
    (dom/p "not loaded")))
