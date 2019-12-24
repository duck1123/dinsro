(ns dinsro.components.user-transactions
  (:require [dinsro.components :as c]
            [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
            [dinsro.components.index-transactions :as c.index-transactions]
            [dinsro.spec.events.forms.add-user-transaction :as s.e.f.add-user-transaction]
            [taoensso.timbre :as timbre]))

(defn section
  [_ transactions]
  [:div.box
   [:h2
    "Transactions"
    [c/show-form-button ::e.f.add-user-transaction/shown? ::e.f.add-user-transaction/set-shown?]]
   [c.f.add-user-transaction/form]
   [:hr]
   [c.index-transactions/index-transactions transactions]])
