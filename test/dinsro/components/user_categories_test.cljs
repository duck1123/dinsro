(ns dinsro.components.user-categories-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.components.user-categories :as c.user-categories]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.events.users :as e.users]
   [dinsro.spec :as ds]
   [dinsro.spec.transactions :as s.transactions]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(let [user (ds/gen-key ::e.users/item)
      user-id (:db/id user)
      categories (ds/gen-key (s/coll-of ::s.transactions/item :count 3))
      store (doto (mock-store)
              e.accounts/init-handlers!
              e.categories/init-handlers!
              e.debug/init-handlers!
              e.f.add-user-category/init-handlers!
              e.f.create-category/init-handlers!
              e.users/init-handlers!)]
  (defcard-rg section
    [c.user-categories/section store user-id categories])

  (deftest section-test
    (is (vector? (c.user-categories/section store user-id categories)))))
