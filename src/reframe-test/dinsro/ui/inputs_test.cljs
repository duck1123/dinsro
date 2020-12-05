(ns dinsro.ui.inputs-test
  (:require
   [cljs.test :refer-macros [is]]
   [clojure.spec.alpha :as s]
   [dinsro.cards :refer-macros [defcard-rg deftest]]
   [dinsro.events.accounts :as e.accounts]
   [dinsro.events.currencies :as e.currencies]
   [dinsro.events.rate-sources :as e.rate-sources]
   [dinsro.events.users :as e.users]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.specs :as ds]
   [dinsro.store :as st]
   [dinsro.store.mock :refer [mock-store]]
   [dinsro.ui.inputs :as u.inputs]
   [taoensso.timbre :as timbre]))

(defn element [name props & children]
  (apply js/React.createElement name (clj->js props) children))

(defn test-store
  []
  (let [store (doto (mock-store)
                e.accounts/init-handlers!
                e.currencies/init-handlers!
                e.rate-sources/init-handlers!
                e.users/init-handlers!)]
    store))

(def rate-sources (ds/gen-key (s/coll-of ::m.rate-sources/item :count 3)))

(let [field :foo
      label "Label"
      accounts (ds/gen-key ::e.accounts/items)
      currencies (ds/gen-key (s/coll-of ::m.currencies/item :count 3))
      users (ds/gen-key ::e.users/items)
      handler [::event-name]]

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.currencies/do-fetch-index-success {:items currencies}])

    (defcard-rg currency-selector
      [u.inputs/currency-selector store label field])

    (deftest currency-selector-test
      (is (vector? (u.inputs/currency-selector store label field)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)

    (defcard-rg checkbox-input
      [u.inputs/checkbox-input store label field handler])

    (deftest checkbox-input-test
      (is (vector? (u.inputs/checkbox-input store label field handler)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.accounts/do-fetch-index-success {:items accounts}])

    (defcard-rg account-selector
      [u.inputs/account-selector store label field])

    (deftest account-selector-test
      (is (vector? (u.inputs/account-selector store label field)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.rate-sources/do-fetch-index-success {:items rate-sources}])

    (defcard-rg rate-source-selector
      [u.inputs/rate-source-selector store label field])

    (deftest rate-source-selector-test
      (is (vector? (u.inputs/rate-source-selector store label field)))))

  (let [store (test-store)]
    (st/reg-basic-sub store field)
    (st/reg-set-event store field)
    (st/dispatch store [::e.users/do-fetch-index-success {:users users}])

    (defcard-rg user-selector
      [u.inputs/user-selector store label field])

    (deftest user-selector-test
      (is (vector? (u.inputs/user-selector store label field))))))
