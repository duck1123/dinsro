(ns dinsro.routes
  (:require [dinsro.actions.authentication :as a.authentication]
            [dinsro.actions.home :as a.home]
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
    ["/authenticate"    {:post   a.authentication/authenticate}]
    ["/register"        {:post   a.authentication/register}]
    ["/users"
     [""                {:get    list-user-response
                         :post   create-user-response}]
     ["/:userId"        {:get    read-user-response
                         :delete {:handler delete-user-response}}]]]])
