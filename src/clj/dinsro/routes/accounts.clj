(ns dinsro.routes.accounts
  (:require [compojure.api.sweet :refer [context GET POST DELETE PATCH]]
            [dinsro.db.core :as db]
            [dinsro.middleware.authenticated :refer [authenticated-mw]]
            [dinsro.models :as m]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(def account-routes
  (context "/api/v1/accounts" []
    :middleware [authenticated-mw]
    :tags ["Accounts"]

    (GET "/" []
      :return [m/Account]
      :summary "Index accounts"
      (let [accounts (db/list-accounts)]
        (ok accounts)))))
