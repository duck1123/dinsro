(ns dinsro.routes.services
  (:require [compojure.api.sweet :refer :all]
            [dinsro.routes.accounts :refer [account-routes]]
            ;; [dinsro.routes.authentication :refer [authentication-routes]]
            [dinsro.routes.users :refer [users-routes]]))

(defn prepare-user
  [registration-data]
  (let [{:keys [password]} registration-data]
    (merge {:password_hash (bcrypt/encrypt password)}
           registration-data)))

(def site-info
  {:version "1.0.0"
   :title "Dinsro"
   :description "Sample Services"})

;; (def service-routes
;;   (api
;;    {:swagger {:ui "/swagger-ui"
;;               :spec "/swagger.json"
;;               :data {:info site-info}}}
;;    account-routes
;;    authentication-routes
;;    users-routes))
