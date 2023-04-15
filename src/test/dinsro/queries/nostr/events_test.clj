(ns dinsro.queries.nostr.events-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest index-ids
  (assertions
   (q.n.events/index-ids) => []))

(deftest count-ids
  (assertions
   (q.n.events/count-ids {}) => 0))

(comment

  (q.n.events/index-ids)

  nil)
