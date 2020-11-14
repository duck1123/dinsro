(ns dinsro.ui.admin-index-accounts-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.admin-accounts :as e.admin-accounts]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.users :as e.users]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.users :as m.users]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]
   [dinsro.ui.admin-index-accounts :as u.admin-index-accounts]
   [taoensso.timbre :as timbre]))

(let [users (ds/gen-key (s/coll-of ::m.users/item :count 3))
      currencies (ds/gen-key (s/coll-of ::m.currencies/item :count 3))
      accounts (map
                (fn [account]
                  (assoc-in account [::m.accounts/user :db/id]
                             (:db/id (rand-nth users))))
                (ds/gen-key (s/coll-of ::m.accounts/item :count 3)))
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
        [u.admin-index-accounts/row-line store account]]])

    (deftest row-line-test
      (is (vector? (u.admin-index-accounts/row-line store account)))))

  (let [store (accounts-store)]
    (defcard-rg index-accounts
      [u.admin-index-accounts/index-accounts store accounts])

    (deftest index-accounts-test
      (is (vector? (u.admin-index-accounts/index-accounts store accounts)))))

  (let [store (accounts-store)]
    (defcard-rg section
      [u.admin-index-accounts/section store])

    (deftest section-test
      (is (vector? (u.admin-index-accounts/section store))))))
