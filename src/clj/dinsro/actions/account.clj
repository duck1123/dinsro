(ns dinsro.actions.account
  (:require [clojure.spec.alpha :as s]
            [dinsro.model.account :as m.accounts]
            [dinsro.specs :as ds]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]))

(defn create-handler
  [{:keys [params] session :session}]
  ;; TODO: fetch
  (let [user-id 1]
    (m.accounts/create-account! (assoc params :user-id user-id))
    (http/ok {:status "ok"})))

(defn index-handler
  [request]
  (let [accounts (m.accounts/index-records)]
    (http/ok {:items accounts})))

(defn read-handler
  [{{:keys [accountId]} :path-params}]
  (if-let [account (m.accounts/read-account {:id accountId})]
    (http/ok account)
    (http/not-found {})))

(defn delete-handler
  [{{:keys [accountId]} :path-params}]
  (m.accounts/delete-account! accountId)
  (http/ok {:status "ok"}))
