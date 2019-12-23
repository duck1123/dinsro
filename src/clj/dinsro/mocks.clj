(ns dinsro.mocks
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [dinsro.model.accounts :as m.accounts]
            [dinsro.model.categories :as m.categories]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.model.rates :as m.rates]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.model.users :as m.users]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rates :as s.rates]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.spec.users :as s.users]
            [dinsro.specs :as ds]
            [orchestra.core :refer [defn-spec]]
            [taoensso.timbre :as timbre]))

(defn-spec mock-account ::s.accounts/item
  []
  (let [params (ds/gen-key ::s.accounts/params)
        id (m.accounts/create-record params)]
    (m.accounts/read-record id)))

(defn-spec mock-category ::s.categories/item
  []
  (let [params (ds/gen-key ::s.categories/params)
        id (m.categories/create-record params)]
    (m.categories/read-record id)))

(defn-spec mock-currency ::s.currencies/item
  []
  (let [params (ds/gen-key ::s.currencies/params)
        id (m.currencies/create-record params)]
    (m.currencies/read-record id)))

(defn-spec mock-rate ::s.rates/item
  []
  (m.rates/read-record
   (m.rates/create-record
    (ds/gen-key ::s.rates/params))))

(defn-spec mock-transaction ::s.transactions/item
  []
  (let [params (ds/gen-key ::s.transactions/params)
        params (assoc-in params [::s.transactions/currency :db/id]
                         (ds/gen-key (into #{1} (m.transactions/index-ids))))
        params (assoc-in params [::s.transactions/account :db/id]
                         (ds/gen-key (into #{1} (m.accounts/index-ids))))
        id (m.transactions/create-record params)]
    (m.transactions/read-record id)))

(defn-spec mock-user ::s.users/item
  []
  (let [params (ds/gen-key ::s.users/params)
        id (m.users/create-record params)]
    (m.users/read-record id)))
