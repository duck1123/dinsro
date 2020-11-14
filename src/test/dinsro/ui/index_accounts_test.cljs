(ns dinsro.ui.index-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.index-accounts :as u.index-accounts]))

(def users (ds/gen-key (s/coll-of ::m.users/item :count 3)))

(def currencies (ds/gen-key (s/coll-of ::m.currencies/item :count 3)))

(def accounts
  (map
   (fn [account]
     (let [user-ids (map :db/id users)
           user-id (rand-nth user-ids)
           currency-ids (map :db/id currencies)
           currency-id (rand-nth (concat [nil] currency-ids))]
       (-> account
           (assoc-in [::m.accounts/user :db/id] user-id)
           (assoc-in [::m.accounts/currency :db/id] currency-id))))
   (ds/gen-key (s/coll-of ::m.accounts/item :count 3))))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.currencies/init-handlers!
                e.debug/init-handlers!
                e.users/init-handlers!)]
    store))

;; row-line

(let [account (first accounts)
      store (test-store)]
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])
  (st/dispatch store [::e.users/do-fetch-index-success {:users users}])
  (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

  (defcard-rg row-line
    [:table.table>tbody
     [u.index-accounts/row-line store account]])

  (deftest row-line-test
    (is (vector (u.index-accounts/row-line store account)))))

;; section

(let [store (test-store)]
  (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])
  (st/dispatch store [::e.users/do-fetch-index-success {:users users}])
  (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

  (defcard-rg section
    [u.index-accounts/section store accounts])

  (deftest section-test
    (is (vector? (u.index-accounts/section store accounts)))))
