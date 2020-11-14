(ns dinsro.ui.user-transactions
  (:require
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.forms.add-user-transaction :as u.f.add-user-transaction]
   [dinsro.ui.index-transactions :as u.index-transactions]
   [taoensso.timbre :as timbre]))

(defn section
  [store _user-id transactions]
  [:div.box
   [:h2
    "Transactions"
    [u.buttons/show-form-button store ::e.f.add-user-transaction/shown?]]
   [u.f.add-user-transaction/form store]
   [:hr]
   [u.index-transactions/index-transactions store transactions]])
