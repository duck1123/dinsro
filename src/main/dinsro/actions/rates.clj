(ns dinsro.actions.rates
  (:require
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [expound.alpha :as expound]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs.actions.rates :as s.a.rates]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(>defn prepare-record
  [params]
  [::s.a.rates/create-params => (? ::m.rates/params)]
  (let [params {::m.rates/currency {:db/id (:currency-id params)}
                ::m.rates/rate (some-> params :rate double)
                ::m.rates/date (some-> params :date tick/instant)}]
    (if (s/valid? ::m.rates/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::m.rates/params params)))
        nil))))

(>defn create-handler
  [{:keys [params]}]
  [::s.a.rates/create-request => ::s.a.rates/create-response]
  (or (when-let [item (some-> params
                              prepare-record
                              q.rates/create-record
                              q.rates/read-record)]
        (http/ok {:item item}))
      (http/bad-request {:status :invalid})))

(>defn index-handler
  [_]
  [::s.a.rates/index-request => ::s.a.rates/index-response]
  (let [;; TODO: parse from request
        limit 50
        items (q.rates/index-records)
        response {:model :rates
                  :limit limit
                  :items items}]
    (http/ok response)))

(>defn read-handler
  [{{:keys [id]} :path-params}]
  [::s.a.rates/read-request => ::s.a.rates/read-response]
  (if-let [id (utils/try-parse-int id)]
    (if-let [item (q.rates/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(>defn delete-handler
  [request]
  [::s.a.rates/delete-request => ::s.a.rates/delete-response]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (q.rates/delete-record id)
    (http/ok {:id id})))

(>defn index-by-currency-handler
  [request]
  [::s.a.rates/index-by-currency-request => ::s.a.rates/index-by-currency-response]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (http/ok {:currency-id id
              :items (sort-by first (q.rates/index-records-by-currency id))})))

(defn index-by-category-handler
  [_]
  (let [;; TODO: parse from request
        limit 50
        items (q.rates/index-records)
        response {:model :rates
                  :limit limit
                  :items items}]
    (http/ok response)))
