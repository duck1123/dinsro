(ns dinsro.components.forms.add-user-category-test
  (:require
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard defcard-rg]]
   [dinsro.components.forms.add-user-category :as c.f.add-user-category]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.categories :as e.categories]
   [dinsro.events.debug :as e.debug]
   [dinsro.events.forms.add-user-category :as e.f.add-user-category]
   [dinsro.events.forms.create-category :as e.f.create-category]
   [dinsro.spec :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.translations :refer [tr]]))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.categories/init-handlers!
                e.debug/init-handlers!
                e.f.add-user-category/init-handlers!
                e.f.create-category/init-handlers!)]
    store))

(let [accounts (ds/gen-key (s/coll-of ::e.accounts/item :count 3))
      user-id 1]

  (comment (defcard accounts accounts))

  (let [store (test-store)]
    (st/dispatch store [::e.f.add-user-category/set-shown? true])
    (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])

    (defcard-rg form
      [c.f.add-user-category/form store user-id])))
