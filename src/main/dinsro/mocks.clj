(ns dinsro.mocks
  (:require
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.transactions :as m.ln.tx]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.queries.accounts :as q.accounts]
   [dinsro.queries.categories :as q.categories]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.transactions :as q.ln.tx]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.queries.transactions :as q.transactions]
   [dinsro.queries.users :as q.users]
   [dinsro.specs :as ds]))

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

(>defn mock-rate-source
  ([]
   [=> ::m.rate-sources/item]
   (let [currency    (mock-currency)
         currency-id (::m.currencies/id currency)]
     (mock-rate-source currency-id)))
  ([currency-id]
   [::m.currencies/id => ::m.rate-sources/item]
   (let [params (ds/gen-key ::m.rate-sources/params)
         params (assoc params ::m.rate-sources/currency currency-id)
         id     (q.rate-sources/create-record params)]
     (q.rate-sources/read-record id))))

(>defn mock-account
  ([]
   [=> ::m.accounts/item]
   (let [user        (mock-user)
         username    (::m.users/id user)
         source (mock-rate-source)
         source-id (::m.rate-sources/id source)
         currency    (mock-currency)
         currency-id (::m.currencies/id currency)]
     (mock-account username currency-id source-id)))
  ([username currency-id source-id]
   [::m.users/id ::m.currencies/id ::m.rate-sources/id

    => ::m.accounts/item]
   (let [params (ds/gen-key ::m.accounts/required-params)
         params (-> params
                    (assoc ::m.accounts/source source-id)
                    (assoc ::m.accounts/user username)
                    (assoc ::m.accounts/currency currency-id))
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
         params (assoc params ::m.rates/currency currency-id)
         id     (q.rates/create-record params)]
     (q.rates/read-record id))))

(>defn mock-transaction
  ([]
   [=> ::m.transactions/item]
   (let [account    (mock-account)
         account-id (::m.accounts/id account)]
     (mock-transaction account-id)))
  ([account-id]
   [::m.accounts/id => ::m.transactions/item]
   (let [params (ds/gen-key ::m.transactions/params)
         params (assoc params ::m.transactions/account account-id)
         id     (q.transactions/create-record params)]
     (q.transactions/read-record id))))

(>defn mock-ln-node
  []
  [=> ::m.ln.nodes/item]
  (let [params (ds/gen-key ::m.ln.nodes/params)
        id (q.ln.nodes/create-record params)]
    (q.ln.nodes/read-record id)))

(>defn mock-ln-tx
  []
  [=> ::m.ln.tx/item]
  (let [params (ds/gen-key ::m.ln.tx/params)
        id (q.ln.tx/create-record params)]
    (q.ln.tx/read-record id)))

(comment
  (ds/gen-key ::m.users/id)
  (ds/gen-key ::m.users/hashed-value)
  (ds/gen-key ::m.users/salt)
  (ds/gen-key ::m.users/iterations)
  (ds/gen-key ::m.users/role)
  (ds/gen-key ::m.users/input-params)
  ;; => {}

  (ds/gen-key ::m.users/params)
  ;; => #:dinsro.model.users{:hashed-value "3L3e8aQbMcKq3Cw5Xeq71", :name "SatfcDHOXsn1U5mFi4JdrOhc7", :salt "o25aF68iy1gTAy", :iterations 725912, :role :account.role/user}

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
  (q.transactions/index-ids)

  (mock-ln-node)
  (ds/gen-key ::m.ln.nodes/item)

  (mock-ln-tx)
  (ds/gen-key ::m.ln.tx/item)

  nil)
