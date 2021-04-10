(ns dinsro.routes
  (:require
   [dinsro.actions.home :as a.home]
   [taoensso.timbre :as timbre]))

(def view-mappings
  ["/"
   "/about"
   "/accounts"
   "/accounts/:id"
   "/admin"
   "/admin/users"
   "/categories"
   "/categories/:id"
   "/currencies"
   "/currencies/:id"
   "/login"
   "/rate-sources"
   "/rate-sources/:id"
   "/rates"
   "/rates/:id"
   "/register"
   "/settings"
   "/transactions"
   "/users"
   "/users/:id"])

(def routes
  [(into [""] (map (fn [path] [path {:get a.home/home-handler}]) view-mappings))])
