(ns dinsro.actions.account
  (:require [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.account :as model.account]
            [dinsro.specs :as ds]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn create-account
  [{:keys [params]}]
  ;; TODO: fetch
  (let [user-id 2]
    (model.account/create-account! (assoc params :user-id user-id))
    (http/ok {:status "ok"})))

(defn index-accounts
  [request]
  (let [accounts (db/list-accounts)]
    (http/ok {:items accounts})))

(defn read-account
  [{{:keys [accountId]} :path-params}]
  (if-let [account (db/read-account {:id accountId})]
    (http/ok account)
    (http/not-found {})))
