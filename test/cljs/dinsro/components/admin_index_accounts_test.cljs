(ns dinsro.components.admin-index-accounts-test
  (:require
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.admin-index-accounts :as c.admin-index-accounts]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.translations :refer [tr]]))

(cards/header "Admin Index Accounts Components" [])

(let [account (ds/gen-key ::s.accounts/item)]
  (defcard account account)

  (defcard-rg c.admin-index-accounts/row-line
    (fn []
      [error-boundary
       [c.admin-index-accounts/row-line account]])))

(let [accounts (ds/gen-key (s/coll-of ::s.accounts/item))]
  (defcard accounts accounts)

  (defcard-rg c.admin-index-accounts/index-accounts
    (fn []
      [error-boundary
       [c.admin-index-accounts/index-accounts accounts]])))

(defcard-rg c.admin-index-accounts/section
  "**Admin Index Rate Sources**"
  (fn []
    [error-boundary
     [c.admin-index-accounts/section]]))
