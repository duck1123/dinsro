(ns dinsro.routes.accounts
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [crypto.password.bcrypt :as bcrypt]
            [dinsro.actions.account.read-account :refer [read-account-response]]
            [dinsro.actions.account.list-account :refer [list-account-response]]
            [dinsro.db.core :as db]
            [dinsro.middleware.authenticated :refer [authenticated-mw]]
            [dinsro.models :as m]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [taoensso.timbre :as timbre]))

(def account-routes
  (context "/api/v1/accounts" []
    :middleware [authenticated-mw]
    :tags ["Accounts"]

    (GET "/" []
      :return [m/Account]
      ;; :header-params [authorization :- ::auth-header]
      :summary "Index accounts"
      (list-account-response))

    (GET "/:accountId" []
      :return m/Account
      :summary "Read Account"
      :path-params [accountId :- s/Int]
      (read-account-response accountId))))
