(ns dinsro.seed
  (:require
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [crux.api :as crux]
   [dinsro.components.crux :as c.crux]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.seed :as seed]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.model.users :as m.users]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.mutations.session :as mu.session]
   [dinsro.mutations.transactions :as mu.transactions]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.users :as q.users]
   [reitit.coercion.spec]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]))

(def links
  [["accounts"     "Accounts"     "/accounts"     :dinsro.views.index-accounts/IndexAccountsPage]
   ["admin"        "Admin"        "/admin"        :dinsro.views.admin/AdminPage]
   ["categories"   "Categories"   "/categories"   :dinsro.views.index-categories/IndexCategoriesPage]
   ["currencies"   "Currencies"   "/currencies"   :dinsro.views.index-currencies/IndexCurrenciesPage]
   ["home"         "Home"         "/"             :dinsro.views.home/HomePage]
   ["login"        "Login"        "/login"        :dinsro.views.login/LoginPage]
   ["rates"        "Rates"        "/rates"        :dinsro.views.index-rates/IndexRatesPage]
   ["rate-sources" "Rate Sources" "/rate-sources" :dinsro.views.index-rate-sources/IndexRateSourcesPage]
   ["registration" "Registration" "/register"     :dinsro.views.registration/RegistrationPage]
   ["settings"     "Settings"     "/settings"     :dinsro.views.settings/SettingsPage]
   ["transactions" "Transactions" "/transactions" :dinsro.views.index-transactions/IndexTransactionsPage]
   ["users"        "User"         "/users"        :dinsro.views.index-users/IndexUsersPage]])

(def category-names ["Category A" "Category B" "Category C"])
(def usernames ["admin" "alice" "bob" "carol" "dave" "eve"])

(def rate-sources
  [{::m.rate-sources/name "CoinLott0"
    ::m.rate-sources/url  "https://www.coinlott0.localhost/api/v1/quotes/BTC-USD"}
   {::m.rate-sources/name "BitPonzi"
    ::m.rate-sources/url  "https://www.bitponzi.biz.localhost/cgi?id=3496709"}
   {::m.rate-sources/name "DuckBitcoin"
    ::m.rate-sources/url  "https://www.duckbitcoin.localhost/api/current-rates"}
   {::m.rate-sources/name "Leviathan"
    ::m.rate-sources/url  "https://www.leviathan.localhost/prices"}])

(defn create-navlinks!
  []
  (let [node (:main c.crux/crux-nodes)
        add  (fnil conj [])
        data (reduce
              (fn [data link]
                (let [[id name href target] link]
                  (update data :navlinks add (seed/new-navlink id name href target))))
              {} links)
        txes (->> data
                  vals
                  flatten
                  (mapv #(vector :crux.tx/put %)))]
    (crux/submit-tx node txes)))

(def account-data
  [["exchange account" 620000.]
   ["hot wallet"       1000000.]
   ["duress account"   (* 6.15 100000000 0.01)]
   ["hodl stack"       (* 6.15 100000000)]])

(def transaction-data
  [["a" 1.0]
   ["b" 2.0]])

(def password m.users/default-password)

(defn seed-db!
  []
  (create-navlinks!)
  (dt/set-timezone! "America/Los_Angeles")

  (doseq [username usernames]
    (mu.session/do-register username password)

    (let [user-eid (q.users/find-eid-by-name username)]
      (doseq [name category-names] (mu.categories/do-create user-eid name))

      (mu.currencies/do-create "sats" "Sats" username)
      (let [currency-id (q.currencies/find-eid-by-code "sats")]
        (doseq [[name value] account-data]
          (mu.accounts/do-create name currency-id user-eid value)))

      (mu.currencies/do-create "usd" "Dollars" username)
      (if-let [currency-id (q.currencies/find-eid-by-code "usd")]
        (do
          (doseq [rate-source rate-sources]
            (mu.rate-sources/do-create
             (assoc rate-source ::m.rate-sources/currency currency-id)))

          (doseq [[name value] [["cash" 3.50]
                                ["Fun Money" 23.67]]]
            (mu.accounts/do-create name currency-id user-eid value))

          (if-let [account-id
                   (nth (:created-item (mu.accounts/do-create "debit" currency-id user-eid 500.))
                        1)]
            (doseq [[description value] transaction-data]
              (mu.transactions/do-create
               {::m.transactions/account     account-id
                ::m.transactions/description description
                ::m.transactions/date        (tick/instant)
                ::m.transactions/value       value}))
            (throw "no account")))
        (throw "no currency"))

      (mu.currencies/do-create "eur" "Euros" username)
      (let [currency-id (q.currencies/find-eid-by-code "eur")]
        (mu.accounts/do-create "travel" currency-id user-eid 0)))))
