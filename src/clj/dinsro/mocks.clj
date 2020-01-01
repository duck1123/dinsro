(ns dinsro.mocks
  (:require [dinsro.model.accounts :as m.accounts]
            [dinsro.model.categories :as m.categories]
            [dinsro.model.currencies :as m.currencies]
            [dinsro.model.rate-sources :as m.rate-sources]
            [dinsro.model.rates :as m.rates]
            [dinsro.model.transactions :as m.transactions]
            [dinsro.model.users :as m.users]
            [dinsro.spec :as ds]
            [dinsro.spec.accounts :as s.accounts]
            [dinsro.spec.categories :as s.categories]
            [dinsro.spec.currencies :as s.currencies]
            [dinsro.spec.rate-sources :as s.rate-sources]
            [dinsro.spec.rates :as s.rates]
            [dinsro.spec.transactions :as s.transactions]
            [dinsro.spec.users :as s.users]
            [taoensso.timbre :as timbre]))

(defn mock-account
  []
  (let [params (ds/gen-key ::s.accounts/params)
        id (m.accounts/create-record params)]
    (m.accounts/read-record id)))

(defn mock-category
  []
  (let [params (ds/gen-key ::s.categories/params)
        id (m.categories/create-record params)]
    (m.categories/read-record id)))

(defn mock-currency
  []
  (let [params (ds/gen-key ::s.currencies/params)
        id (m.currencies/create-record params)]
    (m.currencies/read-record id)))

(defn mock-rate
  []
  (m.rates/read-record
   (m.rates/create-record
    (ds/gen-key ::s.rates/params))))

(defn mock-rate-source
  []
  (let [params (ds/gen-key ::s.rate-sources/params)
        id (m.rate-sources/create-record params)]
    (m.rate-sources/read-record id)))

(defn mock-transaction
  []
  (let [params (ds/gen-key ::s.transactions/params)
        params (assoc-in params [::s.transactions/account :db/id]
                         ;; TODO: get better linked record picker
                         (ds/gen-key (into #{1} (m.accounts/index-ids))))
        id (m.transactions/create-record params)]
    (m.transactions/read-record id)))

(defn mock-user
  []
  (let [params (ds/gen-key ::s.users/params)
        id (m.users/create-record params)]
    (m.users/read-record id)))
