(ns dinsro.components.account-transactions
    (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.forms.add-account-transaction :as c.f.add-account-transaction]
            [dinsro.components.index-transactions :as c.index-transactions]
            [dinsro.spec.events.forms.add-account-transaction :as s.e.f.add-account-transaction]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.translations :refer [tr]]
            [taoensso.timbre :as timbre]))

(defn section
  [account-id items]
  [:div.box
   [:h2
    (tr [:transactions])
    [c/show-form-button
     ::s.e.f.add-account-transaction/shown?
     ::s.e.f.add-account-transaction/set-shown?]]
   [c.f.add-account-transaction/form account-id]
   [:hr]
   (when items [c.index-transactions/index-transactions items])])

(s/fdef section
  :args (s/cat :account-id :db/id
               :items (s/coll-of ::s.transactions/item))
  :ret vector?)
