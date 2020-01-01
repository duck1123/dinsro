(ns dinsro.components.admin-index-accounts-test
  (:require [clojure.spec.alpha :as s]
            [devcards.core :refer-macros [defcard defcard-rg]]
            [dinsro.components.admin-index-accounts :as c.admin-index-accounts]
            [dinsro.spec :as ds]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.translations :refer [tr]]))

(let [account (ds/gen-key ::s.accounts/item)]
  (defcard account account)
  (defcard-rg c.admin-index-accounts/row-line
    [c.admin-index-accounts/row-line account]))

(let [accounts (ds/gen-key (s/coll-of ::s.accounts/item))]
  (comment (defcard accounts accounts))
  (defcard-rg c.admin-index-accounts/index-accounts
    [c.admin-index-accounts/index-accounts accounts]))

(defcard-rg c.admin-index-accounts/section
  "**Admin Index Rate Sources**"
  (fn []
    [:div.box
     [c.admin-index-accounts/section]])
  {})
