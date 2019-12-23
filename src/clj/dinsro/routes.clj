(ns dinsro.routes
  (:require [dinsro.actions.accounts :as a.accounts]
            [dinsro.actions.authentication :as a.authentication]
            [dinsro.actions.categories :as a.categories]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.actions.home :as a.home]
            [dinsro.actions.rates :as a.rates]
            [dinsro.actions.status :as a.status]
            [dinsro.actions.transactions :as a.transactions]
            [dinsro.actions.users :as a.users]
            [dinsro.devcards :as devcards]
            [dinsro.middleware :as middleware]))

(def view-mappings
  ["/"
   "/about"
   "/accounts"
   "/accounts/:id"
   "/categories"
   "/categories/:id"
   "/currencies"
   "/currencies/:id"
   "/login"
   "/rates"
   "/rates/:id"
   "/register"
   "/settings"
   "/transactions"
   "/users"
   "/users/:id"])

(def routes
  [(into [""] (map (fn [path] [path {:get a.home/home-handler}]) view-mappings))
   ["/cards" {:get {:handler devcards/cards-handler}}]
   ["/api/v1" {:middleware [middleware/wrap-formats]}
    ["/accounts" {:middleware [middleware/wrap-restricted]}
     [""                {:post   a.accounts/create-handler
                         :get    a.accounts/index-handler}]
     ["/:id"            {:get    a.accounts/read-handler
                         :delete a.accounts/delete-handler}]]
    ["/authenticate"    {:post   a.authentication/authenticate-handler}]
    ["/categories" {:middleware [middleware/wrap-restricted]}
     [""                {:get    a.categories/index-handler
                         :post   a.categories/create-handler}]
     ["/:id"            {:get    a.categories/read-handler
                         :delete a.categories/delete-handler}]]
    ["/currencies"
     [""                {:get    a.currencies/index-handler
                         :post   a.currencies/create-handler}]
     ["/:id"            {:delete a.currencies/delete-handler
                         :get    a.currencies/read-handler}]]
    ["/logout"          {:post   a.authentication/logout-handler}]
    ["/rates"
     [""                {:get    a.rates/index-handler
                         :post   a.rates/create-handler}]
     ["/:id"            {:get    a.rates/read-handler
                         :delete a.rates/delete-handler}]]
    ["/register"        {:post   a.authentication/register-handler}]
    ["/status"          {:get    a.status/status-handler}]
    ["/transactions" {:middleware [middleware/wrap-restricted]}
     [""                {:get    a.transactions/index-handler
                         :post   a.transactions/create-handler}]
     ["/:id"            {:get    a.transactions/read-handler
                         :delete a.transactions/delete-handler}]]
    ["/users" {:middleware [middleware/wrap-restricted]}
     [""                {:get    a.users/index-handler
                         :post   a.users/create-handler}]
     ["/:id"            {:get    a.users/read-handler
                         :delete a.users/delete-handler}]]]])
