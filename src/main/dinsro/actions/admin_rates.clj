(ns dinsro.actions.admin-rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [expound.alpha :as expound]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs.actions.admin-rates :as s.a.admin-rates]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(>defn prepare-record
  [params]
  [::s.a.admin-rates/create-params => (? ::m.rates/params)]
  (let [params {::m.rates/currency {::m.currencies/id (:currency-id params)}
                ::m.rates/rate     (some-> params :rate double)
                ::m.rates/date     (some-> params :date tick/instant)}]
    (if (s/valid? ::m.rates/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.rates/params params)))
        nil))))

(>defn create-handler
  [request]
  [::s.a.admin-rates/create-request => ::s.a.admin-rates/create-response]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (q.rates/create-record params)]
            (http/ok {:item (q.rates/read-record id)}))))
      (http/bad-request {:status :invalid})))

(>defn read-handler
  [request]
  [::s.a.admin-rates/read-request => ::s.a.admin-rates/read-response]
  (if-let [id (get-in request [:path-params :id])]
    (if-let [item (q.rates/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(>defn delete-handler
  [request]
  [::s.a.admin-rates/delete-request => ::s.a.admin-rates/delete-response]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (q.rates/delete-record id)
    (http/ok {:id id})))

(>defn index-handler
  [_request]
  [::s.a.admin-rates/index-request => ::s.a.admin-rates/index-response]
  (let [;; TODO: parse from request
        limit    50
        items    (q.rates/index-records)
        response {:model :rates
                  :limit limit
                  :items items}]
    (http/ok response)))
