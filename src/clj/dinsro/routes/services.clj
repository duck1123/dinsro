(ns dinsro.routes.services
  (:require [compojure.api.sweet :refer :all]
            [dinsro.routes.accounts :refer [account-routes]]
            [dinsro.routes.users :refer [users-routes]]))

(def site-info
  {:version "1.0.0"
   :title "Dinsro"
   :description "Sample Services"})
