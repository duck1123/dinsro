(ns dinsro.sample
  (:require
   [dinsro.model.navlink :as m.navlink]))

(defn navlink-line
  [id name href]
  {::m.navlink/id   id
   ::m.navlink/name name
   ::m.navlink/href href})

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

