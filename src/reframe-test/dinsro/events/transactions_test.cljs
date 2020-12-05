(ns dinsro.events.transactions-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.events.utils.impl :as eui]
   [dinsro.store.mock :refer [mock-store]]
   [taoensso.timbre :as timbre]))

(let [store (mock-store)]
  (deftest do-fetch-index
    (let [cofx {}
          event [{:foo "bar"}]
          ns-sym 'dinsro.events.transactions
          path-selector [:api-index-transactions]
          response (eui/do-fetch-index
                    ns-sym
                    path-selector
                    store
                    cofx
                    event)]
      (is (contains? response :http-xhrio))
      (is (seq (get-in response [:http-xhrio :uri]))))))
