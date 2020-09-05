(ns dinsro.views.index-accounts-test
  (:require
   [clojure.spec.alpha :as s]
   [cljs.test :refer-macros [is]]
   [devcards.core :refer-macros [defcard defcard-rg deftest]]
   [dinsro.cards :as cards]
   [dinsro.components.boundary :refer [error-boundary]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.spec :as ds]
   [dinsro.spec.accounts :as s.accounts]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.views.index-accounts :as v.index-accounts]
   [reitit.core :as rc]
   [taoensso.timbre :as timbre]))

(cards/header "Index Accounts View" [])

(let [items (ds/gen-key (s/coll-of ::s.accounts/item :count 5))
      template nil
      data nil
      result nil
      path "/"
      path-params {}
      store (doto (mock-store)
              (comment e.accounts/init-handlers!))
      match (rc/->Match template data result path-params path)]

  (defcard items items)

  (comment
    (deftest page
      (is (vector? (v.index-accounts/page store match)))))

  (defcard-rg page-card
    (fn []
      [error-boundary
       (v.index-accounts/page store match)])))
