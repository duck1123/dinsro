(ns dinsro.mocks
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
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

(>defn mock-user
  []
  [=> ::m.users/item]
  (let [params (ds/gen-key ::m.users/params)
        id     (q.users/create-record params)]
    (q.users/read-record id)))

(>defn mock-currency
  []
  [=> ::m.currencies/item]
  (let [params (ds/gen-key ::m.currencies/params)
        id     (q.currencies/create-record params)]
    (q.currencies/read-record id)))

(>defn mock-account
  ([]
   [=> ::m.accounts/item]
   (let [user        (mock-user)
         user-id     (:db/id user)
         currency    (mock-currency)
         currency-id (:db/id currency)]
     (mock-account user-id currency-id)))
  ([user-id currency-id]
   [:db/id :db/id => ::m.accounts/item]
   (let [params (ds/gen-key ::m.accounts/required-params)
         params (-> params
                    (assoc ::m.accounts/user {:db/id user-id})
                    (assoc ::m.accounts/currency {:db/id currency-id}))
         id     (q.accounts/create-record params)]
     (q.accounts/read-record id))))

(defn mock-category
  ([]
   (let [user    (mock-user)
         user-id (:db/id user)]
     (mock-category user-id)))
  ([user-id]
   (let [params (ds/gen-key ::m.categories/params)
         params (assoc-in params [::m.categories/user :db/id] user-id)
         id     (q.categories/create-record params)]
     (q.categories/read-record id))))

(>defn mock-rate
  ([]
   [=> ::m.rates/item]
   (let [currency    (mock-currency)
         currency-id (:db/id currency)]
     (mock-rate currency-id)))
  ([currency-id]
   [:db/id => ::m.rates/item]
   (let [params (ds/gen-key ::m.rates/params)
         params (assoc-in params [::m.rates/currency :db/id] currency-id)
         id     (q.rates/create-record params)]
     (q.rates/read-record id))))

(>defn mock-rate-source
  ([]
   [=> ::m.rate-sources/item]
   (let [currency    (mock-currency)
         currency-id (:db/id currency)]
     (mock-rate-source currency-id)))
  ([currency-id]
   [:db/id => ::m.rate-sources/item]
   (let [params (ds/gen-key ::m.rate-sources/params)
         params (assoc-in params [::m.rate-sources/currency :db/id] currency-id)
         id     (q.rate-sources/create-record params)]
     (q.rate-sources/read-record id))))

(>defn mock-transaction
  ([]
   [=> ::m.transactions/item]
   (let [account    (mock-account)
         account-id (:db/id account)]
     (mock-transaction account-id)))
  ([account-id]
   [:db/id => ::m.transactions/item]
   (let [params (ds/gen-key ::m.transactions/params)
         params (assoc-in params [::m.transactions/account :db/id]
                          account-id)
         id     (q.transactions/create-record params)]
     (q.transactions/read-record id))))

(comment
  (mock-user)
  (mock-currency)
  (mock-rate-source)
  (mock-rate)
  (mock-transaction))
