(ns dinsro.actions.rates
  (:require [clojure.spec.alpha :as s]
            [dinsro.model.rates :as m.rates]
            [dinsro.spec.actions.rates :as s.a.rates]
            [dinsro.spec.rates :as s.rates]
            [orchestra.core :refer [defn-spec]]
            [ring.util.http-response :as http]
            [taoensso.timbre :as timbre]
            [tick.alpha.api :as tick]))

;; Create

(defn-spec prepare-record (s/nilable ::s.rates/params)
  [params :create-rates-request/params]
  (let [params {::s.rates/currency {:db/id (:currency-id params)}
                ::s.rates/rate (some-> params :rate double)
                ::s.rates/date (tick/instant (:date params))}]
    (if (s/valid? ::s.rates/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::s.rates/params params)))
        nil))))

(defn-spec create-handler ::s.a.rates/create-handler-response
  [request ::s.a.rates/create-handler-request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (m.rates/create-record params)]
            (http/ok {:item (m.rates/read-record id)}))))
      (http/bad-request {:status :invalid})))

;; Index

(defn-spec index-handler ::s.a.rates/index-handler-response
  [request ::s.a.rates/index-handler-request]
  (let [items (m.rates/index-records)
        limit 50
        items (sort (fn [a b] (.compareTo (::s.rates/date b) (::s.rates/date a))) items)
        items (take 50 items)]
    (http/ok {:model :rates
              :limit limit
              :items items})))

;; Read

(defn-spec read-handler ::s.a.rates/read-handler-response
  [request ::s.a.rates/read-handler-request]
  (or (let [params (:path-params request)
            id (:id params)
            item (m.rates/read-record id)]
        (http/ok {:item item}))
      (http/not-found {:status :not-found})))

;; Delete

(defn-spec delete-handler ::s.a.rates/delete-handler-response
  [request ::s.a.rates/delete-handler-request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (m.rates/delete-record id)
    (http/ok {:id id})))
