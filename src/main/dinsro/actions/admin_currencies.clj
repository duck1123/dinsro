(ns dinsro.actions.admin-currencies
  (:require
   [clojure.set :as set]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.specs.actions.admin-currencies :as s.a.admin-currencies]
   [expound.alpha :as expound]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(def param-rename-map
  {:name ::m.currencies/name})

;; Prepare

(>defn prepare-record
  [params]
  [::s.a.admin-currencies/create-params => (? ::m.currencies/params)]
  (let [params (-> params
                   (set/rename-keys param-rename-map)
                   (select-keys (vals param-rename-map)))]
    (if (s/valid? ::m.currencies/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.currencies/params params)))
        nil))))

(>defn create-handler
  [request]
  [::s.a.admin-currencies/create-request => ::s.a.admin-currencies/create-response]
  (or (let [{:keys [params]} request]
        (when-let [params (prepare-record params)]
          (let [id (q.currencies/create-record params)]
            (http/ok {:item (q.currencies/read-record id)}))))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.admin-currencies/read-request => ::s.a.admin-currencies/read-response]
  (let [id (some-> request :path-params :id Integer/parseInt)]
    (if-let [item (q.currencies/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))))

(>defn delete-handler
  [request]
  [::s.a.admin-currencies/delete-request => ::s.a.admin-currencies/delete-response]
  (let [{{:keys [id]} :path-params} request]
    (or (try
          (let [id (Integer/parseInt id)]
            (q.currencies/delete-record id)
            (http/ok {:id id}))
          (catch NumberFormatException _ nil))
        (http/bad-request {:status :invalid}))))

(>defn index-handler
  [_request]
  [::s.a.admin-currencies/index-request => ::s.a.admin-currencies/index-response]
  (let [items (q.currencies/index-records)]
    (http/ok {:items items})))
