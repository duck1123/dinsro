(ns dinsro.spec.rates
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            ))

;; (s/def ::name string?)
;; (comment
;;   (gen/generate (s/gen ::name))
;;   )

;; TODO: Must be postitive
(s/def ::value double?)
(comment
  (gen/generate (s/gen ::value))
  )

(s/def ::params (s/keys :req [::value]))
(comment
  (gen/generate (s/gen ::params))
  )


(s/def ::prepared-params (s/keys :req [::value]))
(s/def ::item (s/keys :req [:db/id ::value]))

(def schema
  [{:db/ident ::value
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}])
