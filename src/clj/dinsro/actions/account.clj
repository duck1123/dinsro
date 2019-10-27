(ns dinsro.actions.account
  (:require [clojure.spec.alpha :as s]
            [dinsro.db.core :as db]
            [dinsro.model.account :as model.account]
            [dinsro.specs :as ds]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [params] session :session}]
  (timbre/spy :info session)
  ;; TODO: fetch
  (let [user-id 1]
    (model.account/create-account! (assoc params :user-id user-id))
    (http/ok {:status "ok"})))

(defn index-handler
  [request]
  (let [accounts (db/list-accounts)]
    (http/ok {:items accounts})))

(defn read-handler
  [{{:keys [accountId]} :path-params}]
  (if-let [account (db/read-account {:id accountId})]
    (http/ok account)
    (http/not-found {})))

(defn delete-handler
  [{{:keys [accountId]} :path-params}]
  (model.account/delete-account! accountId)
  (http/ok {:status "ok"}))
