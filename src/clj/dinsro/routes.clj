(ns dinsro.routes
  (:require [dinsro.actions.account :as a.account]
            [dinsro.actions.authentication :as a.authentication]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.actions.home :as a.home]
            [dinsro.actions.rates :as a.rates]
            [dinsro.actions.status :as a.status]
            [dinsro.actions.users :as a.users]
            [dinsro.middleware :as middleware]))

(def routes
  [(into [""]
         (map (fn [path] [path {:get a.home/home-handler}])
              ["/" "/about" "/accounts" "/currencies" "/currencies/:id" "/login"
               "/rates" "/register" "/settings" "/users"]))
   ["/api/v1" {:middleware [middleware/wrap-formats]}
    ["/accounts"
     [""                {:post   a.account/create-handler
                         :get    a.account/index-handler}]
     ["/:accountId"     {:get    a.account/read-handler
                         :delete a.account/delete-handler}]]
    ["/authenticate"    {:post   a.authentication/authenticate-handler}]
    ["/currencies"
     [""                {:get    a.currencies/index-handler
                         :post   a.currencies/create-handler}]]
    ["/logout"          {:post   a.authentication/logout-handler}]
    ["/rates"
     [""                {:get    a.rates/index-handler}]]
    ["/register"        {:post   a.authentication/register-handler}]
    ["/status"          {:get    a.status/status-handler}]
    ["/users" {:middleware [middleware/wrap-restricted]}
     [""                {:get    a.users/index-handler
                         :post   a.users/create-handler}]
     ["/:userId"        {:get    a.users/read-handler
                         :delete a.users/delete-handler}]]]])
