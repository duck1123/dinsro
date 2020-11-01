(ns dinsro.views.show-user-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.forms.add-user-account :as e.f.add-user-account]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.events.forms.add-user-transaction :as e.f.add-user-transaction]
   [dinsro.events.forms.create-account :as e.f.create-account]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.forms.create-transaction :as e.f.create-transaction]
   [dinsro.events.transactions :as e.transactions]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.users :as e.users]
   [dinsro.specs :as ds]
   [dinsro.specs.users :as s.users]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.show-user :as v.show-user]
   [taoensso.timbre :as timbre]))

(let [users (ds/gen-key (s/coll-of ::s.users/item :count 3))
      user (first users)
      store (doto (mock-store)
              e.accounts/init-handlers!
              e.categories/init-handlers!
              e.currencies/init-handlers!
              e.debug/init-handlers!
              e.f.add-user-account/init-handlers!
              e.f.add-user-category/init-handlers!
              e.f.add-user-transaction/init-handlers!
              e.f.create-account/init-handlers!
              e.f.create-category/init-handlers!
              e.f.create-transaction/init-handlers!
              e.transactions/init-handlers!
              e.users/init-handlers!)
      match {:path-params {:id (str (:db/id user))}}]

  (st/dispatch store [::e.users/do-fetch-record-success {:item user}])
  ;; (st/dispatch store [::e.users/do-fetch-index-success {:items users}])

  (defcard-rg page-card
    [v.show-user/page store match])

  (deftest page-test
    (is (vector? (v.show-user/page store match)))))
