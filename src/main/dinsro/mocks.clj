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
         username    (::m.users/id user)
         currency    (mock-currency)
         currency-id (::m.currencies/id currency)]
     (mock-account username currency-id)))
  ([username currency-id]
   [::m.users/id ::m.currencies/id => ::m.accounts/item]
   (let [params (ds/gen-key ::m.accounts/required-params)
         params (-> params
                    (assoc ::m.accounts/user {::m.users/id username})
                    (assoc ::m.accounts/currency {::m.currencies/id currency-id}))
         id     (q.accounts/create-record params)]
     (q.accounts/read-record id))))

(defn mock-category
  ([]
   (let [user    (mock-user)
         user-id (::m.users/id user)]
     (mock-category user-id)))
  ([user-id]
   (let [params (ds/gen-key ::m.categories/params)
         params (assoc-in params [::m.categories/user ::m.users/id] user-id)
         eid     (q.categories/create-record params)]
     (q.categories/read-record eid))))

(>defn mock-rate
  ([]
   [=> ::m.rates/item]
   (let [currency    (mock-currency)
         currency-id (::m.currencies/id currency)]
     (mock-rate currency-id)))
  ([currency-id]
   [::m.currencies/id => ::m.rates/item]
   (let [params (ds/gen-key ::m.rates/params)

         params (assoc params ::m.rates/currency
                       {::m.currencies/id currency-id})
         id     (q.rates/create-record params)]
     (q.rates/read-record id))))

(>defn mock-rate-source
  ([]
   [=> ::m.rate-sources/item]
   (let [currency    (mock-currency)
         currency-id (::m.currencies/id currency)]
     (mock-rate-source currency-id)))
  ([currency-id]
   [::m.currencies/id => ::m.rate-sources/item]
   (let [params (ds/gen-key ::m.rate-sources/params)
         params (assoc params ::m.rate-sources/currency {::m.currencies/id currency-id})
         id     (q.rate-sources/create-record params)]
     (q.rate-sources/read-record id))))

(>defn mock-transaction
  ([]
   [=> ::m.transactions/item]
   (let [account    (mock-account)
         account-id (::m.accounts/id account)]
     (mock-transaction account-id)))
  ([account-id]
   [::m.accounts/id => ::m.transactions/item]
   (let [params (ds/gen-key ::m.transactions/params)
         params (assoc-in params [::m.transactions/account :db/id]
                          (q.accounts/find-eid-by-id account-id))
         id     (q.transactions/create-record params)]
     (q.transactions/read-record id))))

(comment
  (mock-user)
  (q.users/index-ids)
  (q.users/read-record 67)

  (mock-currency)
  (q.currencies/index-ids)
  (q.currencies/read-record 89)

  (mock-account)
  (q.accounts/index-ids)
  (q.accounts/read-record 60)

  (mock-rate-source)
  (mock-rate-source "a9a4cdeb-5d82-4725-b834-d1fd22dbf26b")
  (q.rate-sources/index-ids)
  (q.rate-sources/read-record 111)

  (mock-rate)
  (q.rates/index-ids)

  (mock-transaction)
  (q.transactions/index-ids))
