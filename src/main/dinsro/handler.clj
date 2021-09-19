(ns dinsro.handler
  (:require
   [com.fulcrologic.rad.type-support.date-time :as dt]
   [crux.api :as crux]
   [dinsro.env :refer [defaults]]
   [dinsro.components.crux :as c.crux]
   [dinsro.layout :refer [error-page] :as layout]
   [dinsro.middleware :as middleware]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.seed :as seed]
   [dinsro.model.transactions :as m.transactions]
   [dinsro.mutations.accounts :as mu.accounts]
   [dinsro.mutations.categories :as mu.categories]
   [dinsro.mutations.currencies :as mu.currencies]
   [dinsro.mutations.rate-sources :as mu.rate-sources]
   [dinsro.mutations.session :as mu.session]
   [dinsro.mutations.transactions :as mu.transactions]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.users :as q.users]
   [dinsro.routes :as routes]
   [mount.core :as mount]
   [reitit.coercion.spec]
   [reitit.ring :as ring]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  (ring/ring-handler
   (ring/router routes/routes)
   (ring/routes
    (ring/create-resource-handler {:path "/"})
    (ring/create-default-handler
     {:not-found
      (constantly (error-page {:status 404, :title "404 - Page not found"}))
      :method-not-allowed
      (constantly (error-page {:status 405, :title "405 - Not allowed"}))
      :not-acceptable
      (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

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

(defn seed-db!
  []
  (let [username   "admin"
        password   "hunter2"
        categories ["Category A"
                    "Category B"
                    "Category C"]]

    (create-navlinks!)
    (dt/set-timezone! "America/Los_Angeles")
    (mu.session/do-register username password)

    (let [user-eid (q.users/find-eid-by-name username)]
      (doseq [name categories] (mu.categories/do-create user-eid name))

      (mu.currencies/do-create "sats" "Sats" username)
      (let [currency-id (q.currencies/find-eid-by-code "sats")]
        (mu.accounts/do-create "exchange account" currency-id user-eid 620000.)
        (mu.accounts/do-create "hot wallet" currency-id user-eid 1000000.)
        (mu.accounts/do-create "duress account" currency-id user-eid (* 6.15 100000000 0.01))
        (mu.accounts/do-create "hodl stack" currency-id user-eid (* 6.15 100000000)))

      (mu.currencies/do-create "usd" "Dollars" username)

      (if-let [currency-id (q.currencies/find-eid-by-code "usd")]
        (do
          (mu.rate-sources/do-create
           {::m.rate-sources/name     "CoinLott0"
            ::m.rate-sources/url      "https://www.coinlott0.localhost/api/v1/quotes/BTC-USD"
            ::m.rate-sources/currency currency-id})
          (mu.rate-sources/do-create
           {::m.rate-sources/name     "BitPonzi"
            ::m.rate-sources/url      "https://www.bitponzi.biz.localhost/cgi?id=3496709"
            ::m.rate-sources/currency currency-id})
          (mu.rate-sources/do-create
           {::m.rate-sources/name     "DuckBitcoin"
            ::m.rate-sources/url      "https://www.duckbitcoin.localhost/api/current-rates"
            ::m.rate-sources/currency currency-id})
          (mu.rate-sources/do-create
           {::m.rate-sources/name     "Leviathan"
            ::m.rate-sources/url      "https://www.leviathan.localhost/prices"
            ::m.rate-sources/currency currency-id})

          (mu.accounts/do-create "cash" currency-id user-eid 3.50)
          (mu.accounts/do-create "Fun Money" currency-id user-eid 23.67)

          (if-let [account-id
                   (nth (:created-item (mu.accounts/do-create "debit" currency-id user-eid 500.))
                        1)]
            (do
              (mu.transactions/do-create
               {::m.transactions/account     account-id
                ::m.transactions/description "a"
                ::m.transactions/date        (tick/instant)
                ::m.transactions/value       1.0})
              (mu.transactions/do-create
               {::m.transactions/account     account-id
                ::m.transactions/description "b"
                ::m.transactions/date        (tick/instant)
                ::m.transactions/value       2.0}))
            (throw "no account")))
        (throw "no currency"))

      (mu.currencies/do-create "eur" "Euros" username)
      (let [currency-id (q.currencies/find-eid-by-code "eur")]
        (mu.accounts/do-create "travel" currency-id user-eid 0)))))

(defn app []
  (log/info "starting app handler")
  (seed-db!)
  (middleware/wrap-base #'app-routes))
