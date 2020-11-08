(ns dinsro.mocks
  (:require
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [dinsro.specs.accounts :as s.accounts]
   [dinsro.specs.categories :as s.categories]
   [dinsro.specs.currencies :as s.currencies]
   [dinsro.specs.rate-sources :as s.rate-sources]
   [dinsro.specs.rates :as s.rates]
   [dinsro.specs.transactions :as s.transactions]
   [dinsro.specs.users :as s.users]
   [taoensso.timbre :as timbre]))

(defn mock-user
  []
  (let [params (ds/gen-key ::s.users/params)
        id (q.users/create-record params)]
    (q.users/read-record id)))

(defn mock-account
  ([]
   (let [user (mock-user)]
     (mock-account (:db/id user))))
  ([user-id]
   (let [params (assoc (ds/gen-key ::s.accounts/params) ::s.accounts/user {:db/id user-id})
         id (q.accounts/create-record params)]
     (q.accounts/read-record id))))

(defn mock-category
  []
  (let [params (ds/gen-key ::s.categories/params)
        id (q.categories/create-record params)]
    (q.categories/read-record id)))

(defn mock-currency
  []
  (let [params (ds/gen-key ::s.currencies/params)
        id (q.currencies/create-record params)]
    (q.currencies/read-record id)))

(defn mock-rate
  []
  (q.rates/read-record
   (q.rates/create-record
    (ds/gen-key ::s.rates/params))))

(defn mock-rate-source
  []
  (let [params (ds/gen-key ::s.rate-sources/params)
        id (q.rate-sources/create-record params)]
    (q.rate-sources/read-record id)))

(defn mock-transaction
  []
  (let [params (ds/gen-key ::s.transactions/params)
        params (assoc-in params [::s.transactions/account :db/id]
                         ;; TODO: get better linked record picker
                         (ds/gen-key (into #{1} (q.accounts/index-ids))))
        id (q.transactions/create-record params)]
    (q.transactions/read-record id)))
