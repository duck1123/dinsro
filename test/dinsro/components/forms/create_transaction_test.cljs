(ns dinsro.components.forms.create-transaction-test
  (:require
   [cljs.pprint :as p]
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards :include-macros true]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.forms.create-transaction :as c.f.create-transaction]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(cards/header
 'dinsro.components.forms.create-transaction-test
 "Create Transaction Form Components"
 [#{:transactions} #{:transactions :components}])

(def accounts (ds/gen-key (s/coll-of ::s.accounts/item :count 3)))
(def transactions (ds/gen-key (s/coll-of ::s.transactions/item :count 3)))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.debug/init-handlers!
                e.f.create-transaction/init-handlers!)]
    store))

(comment (defcard accounts-card accounts))
(comment (defcard transactions-card transactions))

(let [store (test-store)]
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])
  (st/dispatch store [::e.f.create-transaction/set-shown? true])

  (defcard-rg form-data-card
    (fn []
      [error-boundary
       [:pre (with-out-str (p/pprint @(st/subscribe store [::e.f.create-transaction/form-data])))]]))

  (defcard-rg create-transaction-card
    (fn []
      [error-boundary
       [c.f.create-transaction/form store]]))

  (deftest create-transaction-test
    (is (vector? (c.f.create-transaction/form store)))))
