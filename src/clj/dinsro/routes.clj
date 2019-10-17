(ns dinsro.routes
  (:require [dinsro.actions.account :as a.account]
            [dinsro.actions.authentication :as a.authentication]
            [dinsro.actions.currencies :as a.currencies]
            [dinsro.actions.home :as a.home]
            [dinsro.actions.status :as a.status]
            [dinsro.actions.users :as a.users]))

(def routes
  [["" {:middleware [#_middleware/wrap-csrf
                     middleware/wrap-formats]}
    ["/" {:get a.home/home-page}]]
   ["/api/v1" {:middleware [middleware/wrap-formats]}
    ["/accounts" {}
     ["" {:post a.account/create
          :get  a.account/index}]
     ["/:accountId" {:get    a.account/read
                     :delete a.account/delete}]]
    ["/authenticate" {}
     ["" {:post a.authentication/authenticate}]]
    ["/currencies" {}
     ["" {:get a.currencies/index}]]
    ["/logout" {}
     ["" {:post a.authentication/logout}]]
    ["/register" {}
     ["" {:post a.authentication/register}]]
    ["/status" {}
     ["" a.status/status-response]]
    ["/users" {:middleware [middleware/wrap-restricted]}
     [""         {:get  {:handler a.users/index}
                  :post {:handler a.users/create}}]
     ["/:userId" {:get    {:handler a.users/read}
                  :delete {:handler a.users/delete}}]]]])
