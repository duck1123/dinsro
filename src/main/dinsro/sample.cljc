(ns dinsro.sample
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.categories :as m.categories]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]))

(def user-map
  {1 {::m.users/id 1
      ::m.users/name "Foo McFooerson"
      ::m.users/email "foo@example.com"}
   2 {::m.users/id 2
      ::m.users/name "Bart "
      ::m.users/email "bar@example.com"}})

(def currency-map
  {1 {::m.currencies/id 1
      ::m.currencies/name "Dollars"}
   2 {::m.currencies/id 2
      ::m.currencies/name "Euros"}
   3 {::m.currencies/id 3
      ::m.currencies/name "Yen"}})

(defn rate-line
  [id name currency-id url]
  {::m.rate-sources/id id
   ::m.rate-sources/name name
   ::m.rate-sources/currency-id currency-id
   ::m.rate-sources/url url})

(def rate-map
  {1 {::m.rates/id 1
      ::m.rates/currency 1
      ::m.rates/rate 1.01
      ::m.rates/date "2020-12-05 22:11:04"}})

(def rate-source-map
  {1 (rate-line 1 "CoinLott0" 1 "https://www.coinlott0.localhost/api/v1/quotes/BTC-USD")
   2 (rate-line 2 "BitPonzi" 1 "https://www.bitponzi.biz.localhost/cgi?id=3496709")
   3 (rate-line 3 "DuckBitcoin" 1 "https://www.duckbitcoin.localhost/api/current-rates")
   4 (rate-line 4 "Leviathan" 1 "https://www.leviathan.localhost/prices")})

(def category-map
  {1 {::m.categories/id 1
      ::m.categories/name "Savings"
      ::m.categories/user-id 1}
   2 {::m.categories/id 2
      ::m.categories/name "Spending"
      ::m.categories/user-id 2}})

(def account-map
  {1 {::m.accounts/id 1
      ::m.accounts/initial-value 1
      ::m.accounts/name "Savings Account"
      ::m.accounts/currency-id 2
      ::m.accounts/user-id 1}
   2 {::m.accounts/id 2
      ::m.accounts/initial-value 20
      ::m.accounts/name "Fun Money"
      ::m.accounts/currency-id 1
      ::m.accounts/user-id 2}})

(def transaction-map
  {1 {::m.transactions/id 1
      ::m.transactions/description "bought a thing"
      ::m.transactions/date "2020-11-28 00:00:00"
      ::m.transactions/account-id 1}
   2 {::m.transactions/id 2
      ::m.transactions/description "sold some things"
      ::m.transactions/date "2020-11-28 01:00:00"
      ::m.transactions/account-id 1}})
