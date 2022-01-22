(ns dinsro.sample
  (:require
   [dinsro.model.accounts :as m.accounts]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.navlink :as m.navlink]
   [dinsro.model.users :as m.users]))

(defn account-line
  [id initial-value name currency-id user-id]
  {::m.accounts/id            id
   ::m.accounts/initial-value initial-value
   ::m.accounts/name          name
   ::m.accounts/currency      {::m.currencies/id currency-id}
   ::m.accounts/user          {::m.users/id user-id}})

(defn navlink-line
  [id name href]
  {::m.navlink/id   id
   ::m.navlink/name name
   ::m.navlink/href href})

(defn user-line
  [user-id]
  {::m.users/user-id user-id})

(def account-map
  {1 (account-line 1 1  "Savings Account" 2 1)
   2 (account-line 2 20 "Fun Money"       1 2)})

(def navlink-map
  {"accounts"     (navlink-line "accounts"     "Accounts"     "/accounts")
   "admin"        (navlink-line "admin"        "Admin"        "/admin")
   "bar"          (navlink-line "bar"          "bar"          "/bar")
   "baz"          (navlink-line "baz"          "baz"          "/baz")
   "categories"   (navlink-line "categories"   "Categories"   "/categories")
   "currencies"   (navlink-line "currencies"   "Currencies"   "/currencies")
   "foo"          (navlink-line "foo"          "foo"          "/foo")
   "home"         (navlink-line "home"         "Home"         "/")
   "login"        (navlink-line "login"        "Login"        "/login")
   "rates"        (navlink-line "rates"        "Rates"        "/rates")
   "rate-sources" (navlink-line "rate-sources" "Rate Sources" "/rate-sources")
   "registration" (navlink-line "registration" "Registration" "/register")
   "settings"     (navlink-line "settings"     "Settings"     "/settings")
   "transactions" (navlink-line "transactions" "Transactions" "/transactions")
   "users"        (navlink-line "users"        "User"         "/users")})

(def user-map
  {1 (user-line "foo")
   2 (user-line "bar")})
