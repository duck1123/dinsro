(ns dinsro.actions.rates
  (:require
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs.actions.rates :as s.a.rates]
   [dinsro.specs.rates :as s.rates]
   [dinsro.utils :as utils]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

;; Create

(defn prepare-record
  [params]
  (let [params {::s.rates/currency {:db/id (:currency-id params)}
                ::s.rates/rate (some-> params :rate double)
                ::s.rates/date (some-> params :date tick/instant)}]
    (if (s/valid? ::s.rates/params params)
      params
      (do
        (comment (timbre/warnf "not valid: %s" (expound/expound-str ::s.rates/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.rates/create-params)
  :ret  (s/nilable ::s.rates/params))

(defn create-handler
  [{:keys [params]}]
  (or (when-let [item (some-> params
                              prepare-record
                              q.rates/create-record
                              q.rates/read-record)]
        (http/ok {:item item}))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.rates/create-request)
  :ret ::s.a.rates/create-response)

;; Index

(defn index-handler
  [_]
  (let [
        ;; TODO: parse from request
        limit 50
        items (q.rates/index-records)
        response {:model :rates
                  :limit limit
                  :items items}]
    (http/ok response)))

(s/fdef index-handler
  :args (s/cat :request ::s.a.rates/index-request)
  :ret ::s.a.rates/index-response)

;; Read

(defn read-handler
  [{{:keys [id]} :path-params}]
  (if-let [id (utils/try-parse-int id)]
    (if-let [item (q.rates/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.rates/read-request)
  :ret ::s.a.rates/read-response)

;; Delete

(defn delete-handler
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (q.rates/delete-record id)
    (http/ok {:id id})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.rates/delete-request)
  :ret ::s.a.rates/delete-response)

;; Index by Currency

(defn index-by-currency-handler
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
   (http/ok {:currency-id id
             :items (sort-by first (q.rates/index-records-by-currency id))})))

(s/fdef index-by-currency-handler
  :args (s/cat :request ::s.a.rates/index-by-currency-request)
  :ret ::s.a.rates/index-by-currency-response)

(defn index-by-category-handler
  [_]
  (let [
        ;; TODO: parse from request
        limit 50
        items (q.rates/index-records)
        response {:model :rates
                  :limit limit
                  :items items}]
    (http/ok response)))
