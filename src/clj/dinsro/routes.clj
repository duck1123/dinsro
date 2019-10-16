(ns dinsro.routes
  (:require [dinsro.actions.accounts :as a.accounts]
            [dinsro.actions.authentication :as a.authentication]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.actions.home :as a.home]
            [dinsro.actions.status :as actions.status]
            [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.actions.user.delete-user :refer [delete-user-response]]
            [dinsro.actions.user.list-user :refer [list-user-response]]
            [dinsro.actions.user.read-user :refer [read-user-response]]
            [dinsro.middleware :as middleware]))

(def routes
  [(into [""]
         (map (fn [path] [path {:get a.home/home-handler}])
              ["/" "/about" "/login" "/register" "/users"]))
   ["/api/v1" {:middleware [middleware/wrap-formats]}
    ["/accounts"
     [""                {:post   a.accounts/create-account
                         :get    a.accounts/index-accounts}]
     ["/:accountId"     {:get    a.accounts/read-account
                         :delete actions.account/delete-account}]]
    ["/authenticate"    {:post   a.authentication/authenticate}]
    ["/currencies"
     [""                {:get    a.currencies/index-currencies}]]
    ["/logout"          {:get    a.authentication/logout}]
    ["/register"        {:post   a.authentication/register}]
    ["/status"          {:get    actions.status/status-response}]
    ["/users" {:middleware [middleware/wrap-restricted]}
     [""                {:get    list-user-response
                         :post   create-user-response}]
     ["/:userId"        {:get    read-user-response
                         :delete {:handler delete-user-response}}]]]])
