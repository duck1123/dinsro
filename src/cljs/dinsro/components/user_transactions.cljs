(ns dinrso.components.user-transactions
  (:require [clojure.spec.alpha :as s]
            [dinsro.components :as c]
            [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
            [dinsro.components.index-transactions :as c.index-transactions]
            [dinsro.spec.transactions :as s.transactions]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))


(defn-spec transactions-section vector?
  [user-id pos-int? transactions (s/coll-of ::s.transactions/item)]
  [:div.box
   [:h2
    "Transactions"
    [c/show-form-button ::c.f.add-user-transaction/shown? ::c.f.add-user-transaction/set-shown?]]
   [c.f.add-user-transaction/form]
   [:hr]
   [c.index-transactions/index-transactions transactions]])
