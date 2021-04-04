(ns dinsro.actions.admin-rate-sources
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [expound.alpha :as expound]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.specs.actions.admin-rate-sources :as s.a.admin-rate-sources]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]))

(>defn prepare-record
  [params]
  [::s.a.admin-rate-sources/create-params => (? ::m.rate-sources/params)]
  (let [params {::m.rate-sources/currency {:db/id (:currency-id params)}
                ::m.rate-sources/name     (some-> params :name)
                ::m.rate-sources/url      (some-> params :url)}]
    (if (s/valid? ::m.rate-sources/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.rate-sources/params params)))
        nil))))

(>defn create-handler
  [request]
  [::s.a.admin-rate-sources/create-request => ::s.a.admin-rate-sources/create-response]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (q.rate-sources/create-record params)]
            (http/ok {:item (q.rate-sources/read-record id)}))))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.admin-rate-sources/read-request => ::s.a.admin-rate-sources/read-response]
  (if-let [id (get-in request [:path-params :id])]
    (if-let [item (q.rate-sources/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(>defn delete-handler
  [request]
  [::s.a.admin-rate-sources/delete-request => ::s.a.admin-rate-sources/delete-response]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (q.rate-sources/delete-record id)
    (http/ok {:id id})))

(>defn index-handler
  [_request]
  [::s.a.admin-rate-sources/index-request => ::s.a.admin-rate-sources/index-response]
  (let [;; TODO: parse from request
        limit    50
        items    (q.rate-sources/index-records)
        response {:model :rate-sources
                  :limit limit
                  :items items}]
    (http/ok response)))
