(ns dinsro.mocks
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.account :as m.accounts]
            [dinsro.model.categories :as m.categories]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.categories :as s.categories]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec mock-account ::s.accounts/item
  []
  (let [params (gen/generate (s/gen ::s.accounts/params))
        id (m.accounts/create-record params)]
    (m.accounts/read-record id)))

(defn-spec mock-category ::s.categories/item
  []
  (let [params (gen/generate (s/gen ::s.categories/params))
        id (m.categories/create-record params)]
    (m.categories/read-record id)))

(defn-spec mock-currency ::s.currencies/item
  []
  (let [params (gen/generate (s/gen ::s.currencies/params))
        id (m.currencies/create-record params)]
    (m.currencies/read-record id)))

(defn-spec mock-rates ::s.rates/item
  []
  (m.rates/read-record
   (m.rates/create-record
    (gen/generate (s/gen ::s.rates/params)))))

(defn-spec mock-transaction ::s.transactions/item
  []
  (m.transactions/read-record
   (m.transactions/create-record
    (gen/generate (s/gen ::s.transactions/params)))))

(defn-spec mock-user ::s.users/item
  []
  (let [params (gen/generate (s/gen ::s.users/params))
        id (m.users/create-record params)]
    (m.users/read-record id)))

(comment
  (mock-account)
  )
