(ns dinsro.components.index-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.components.index-accounts :as c.index-accounts]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.spec.currencies :as s.currencies]
   [dinsro.spec.users :as s.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(cards/header "Index Accounts Components" [])

(def users (ds/gen-key (s/coll-of ::s.users/item :count 3)))

(def currencies (ds/gen-key (s/coll-of ::s.currencies/item :count 3)))

(def accounts
  (map
   (fn [account]
     (let [user-ids (map :db/id users)
           user-id (rand-nth user-ids)
           currency-ids (map :db/id currencies)
           currency-id (rand-nth (concat [nil] currency-ids))]
       (-> account
           (assoc-in [::s.accounts/user :db/id] user-id)
           (assoc-in [::s.accounts/currency :db/id] currency-id))))
   (ds/gen-key (s/coll-of ::s.accounts/item :count 3))))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.users/init-handlers!)]
    store))

(comment (defcard accounts-card accounts))
(comment (defcard currencies-card currencies))
(comment (defcard users-card users))

;; row-line

(let [account (first accounts)
      store (test-store)]
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])
  (st/dispatch store [::e.users/do-fetch-index-success {:users users}])
  (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

  (defcard-rg row-line
    (fn []
      [error-boundary
       [:table.table>tbody
        [c.index-accounts/row-line store account]]]))

  (deftest row-line-test
    (is (vector (c.index-accounts/row-line store account)))))

;; section

(let [store (test-store)]
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])
  (st/dispatch store [::e.users/do-fetch-index-success {:users users}])
  (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

  (defcard-rg section
    (fn []
      [error-boundary
       [c.index-accounts/section store accounts]]))

  (deftest section-test
    (is (vector? (c.index-accounts/section store accounts)))))
