(ns dinrso.components.user-transactions
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.components :as c]
            [dinsro.components.buttons :as c.buttons]
            [dinsro.components.forms.add-user-transaction :as c.f.add-user-transaction]
            [dinsro.components.index-transactions :as c.index-transactions]
            [dinsro.components.show-user :refer [show-user]]
            [dinsro.events.accounts :as e.accounts]
            [dinsro.events.currencies :as e.currencies]
            [dinsro.events.debug :as e.debug]
            [dinsro.events.users :as e.users]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.specs :as ds]
            [kee-frame.core :as kf]
            [orchestra.core :refer [defn-spec]]
            [re-frame.core :as rf]
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
