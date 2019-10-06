(ns dinsro.routes.user
  (:require [dinsro.actions.user.create-user :refer [create-user-response]]
            [dinsro.actions.user.list-user :refer [list-user-response]]
            [dinsro.actions.user.read-user :refer [read-user-response]]))

(defn user-routes  []
  ["/users" {}
   [""        {:get  {:handler list-user-response}
               :post {:handler create-user-response}}]
   ["/:userId" {:get  {:handler read-user-response}}]])
