(ns dinsro.joins.nostr.events-test
  (:require
   [clojure.test :refer [deftest use-fixtures]]
   [com.fulcrologic.rad.attributes :as attr]
   [com.fulcrologic.rad.attributes-options :as ao]
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.test-helpers :as th]
   [fulcro-spec.core :refer [assertions]]))

(def schemata [])

(use-fixtures :each (fn [f] (th/start-db f schemata)))

(deftest index
  (let [attribute j.n.events/index
        resolver  (ao/pc-resolve attribute)
        qk        (::attr/qualified-key attribute)
        env       {:query-params {}}
        params    {}]
    (assertions
     (resolver env params) => {qk {:results [] :total 0}})))

(deftest content-hiccup
  (let [resolver (ao/pc-resolve j.n.events/content-hiccup)
        content  "foo"
        env      {}
        params   {::m.n.events/content content}]
    (assertions
     (resolver env params) =>
     {::j.n.events/content-hiccup
      [:div [:p "foo"]]})))
