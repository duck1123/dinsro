(ns dinsro.components.admin-index-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.admin-index-accounts :as c.admin-index-accounts]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.users :as e.users]
   [dinsro.specs :as ds]
   [dinsro.specs.accounts :as s.accounts]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.specs.users :as s.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [taoensso.timbre :as timbre]))

(let [users (ds/gen-key (s/coll-of ::s.users/item :count 3))
      currencies (ds/gen-key (s/coll-of ::s.currencies/item :count 3))
      accounts (map
                (fn [account]
                  (assoc-in account [::s.accounts/user :db/id]
                             (:db/id (rand-nth users))))
                (ds/gen-key (s/coll-of ::s.accounts/item :count 3)))
      account (first accounts)
      accounts-store
      (fn []
        (let [store (doto (mock-store)
                      e.accounts/init-handlers!
                      e.admin-accounts/init-handlers!
                      e.currencies/init-handlers!
                      e.debug/init-handlers!
                      e.f.add-user-account/init-handlers!
                      e.f.create-account/init-handlers!
                      e.users/init-handlers!)]
          (st/dispatch store [::e.users/do-fetch-index-success {:items users}])

          (doseq [user users]
            (st/dispatch store [::e.users/do-fetch-record-success {:item user}]))

          (st/dispatch store [::e.admin-accounts/do-fetch-index-success {:items accounts}])
          (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

          store))]
  (let [store (accounts-store)]
    (defcard-rg row-line
      [:table.table
       [:tbody
        [c.admin-index-accounts/row-line store account]]])

    (deftest row-line-test
      (is (vector? (c.admin-index-accounts/row-line store account)))))

  (let [store (accounts-store)]
    (defcard-rg c.admin-index-accounts/index-accounts
      [c.admin-index-accounts/index-accounts store accounts])

    (deftest index-accounts-test
      (is (vector? (c.admin-index-accounts/index-accounts store accounts)))))

  (let [store (accounts-store)]
    (defcard-rg c.admin-index-accounts/section
      [c.admin-index-accounts/section store])

    (deftest section-test
      (is (vector? (c.admin-index-accounts/section store))))))
