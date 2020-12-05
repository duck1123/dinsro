(ns dinsro.events.authentication-test
  (:require
   [cljs.test :refer-macros [is]]
   [dinsro.cards :refer-macros [deftest]]
   [dinsro.events.authentication :as e.authentication]
   [dinsro.store.mock :refer [mock-store]]))

(deftest do-authenticate-test
  (let [store (mock-store)
        data nil
        cofx nil
        event [data nil]
        response (e.authentication/do-authenticate
                  store cofx event)]
    (is (map? response))))
