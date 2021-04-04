(ns dinsro.mocks
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]
   [taoensso.timbre :as timbre]))

(defn mock-user
  []
  (let [params (ds/gen-key ::m.users/params)
        id     (q.users/create-record params)]
    (q.users/read-record id)))

(defn mock-account
  ([]
   (let [user (mock-user)]
     (mock-account (:db/id user))))
  ([user-id]
   (let [params (assoc (ds/gen-key ::m.accounts/params) ::m.accounts/user {:db/id user-id})
         id     (q.accounts/create-record params)]
     (q.accounts/read-record id))))

(defn mock-category
  []
  (let [params (ds/gen-key ::m.categories/params)
        id     (q.categories/create-record params)]
    (q.categories/read-record id)))

(defn mock-currency
  []
  (let [params (ds/gen-key ::m.currencies/params)
        id     (q.currencies/create-record params)]
    (q.currencies/read-record id)))

(defn mock-rate
  []
  (q.rates/read-record
   (q.rates/create-record
    (ds/gen-key ::m.rates/params))))

(defn mock-rate-source
  []
  (let [params (ds/gen-key ::m.rate-sources/params)
        id     (q.rate-sources/create-record params)]
    (q.rate-sources/read-record id)))

(defn mock-transaction
  []
  (let [params (ds/gen-key ::m.transactions/params)
        params (assoc-in params [::m.transactions/account :db/id]
                         ;; TODO: get better linked record picker
                         (ds/gen-key (into #{1} (q.accounts/index-ids))))
        id     (q.transactions/create-record params)]
    (q.transactions/read-record id)))
