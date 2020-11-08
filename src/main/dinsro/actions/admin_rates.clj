(ns dinsro.actions.admin-rates
  (:require
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs.actions.admin-rates :as s.a.admin-rates]
   [dinsro.specs.rates :as s.rates]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

;; Prepare

(defn prepare-record
  [params]
  (let [params {::s.rates/currency {:db/id (:currency-id params)}
                ::s.rates/rate (some-> params :rate double)
                ::s.rates/date (some-> params :date tick/instant)}]
    (if (s/valid? ::s.rates/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::s.rates/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.admin-rates/create-params)
  :ret  (s/nilable ::s.rates/params))

;; Create

(defn create-handler
  [request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (q.rates/create-record params)]
            (http/ok {:item (q.rates/read-record id)}))))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.admin-rates/create-request)
  :ret ::s.a.admin-rates/create-response)

;; Read

(defn read-handler
  [request]
  (if-let [id (get-in request [:path-params :id])]
    (if-let [item (q.rates/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.admin-rates/read-request)
  :ret ::s.a.admin-rates/read-response)

;; Delete

(defn delete-handler
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (q.rates/delete-record id)
    (http/ok {:id id})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.admin-rates/delete-request)
  :ret ::s.a.admin-rates/delete-response)

;; Index

(defn index-handler
  [_request]
  (let [
        ;; TODO: parse from request
        limit 50
        items (q.rates/index-records)
        response {:model :rates
                  :limit limit
                  :items items}]
    (http/ok response)))

(s/fdef index-handler
  :args (s/cat :request ::s.a.admin-rates/index-request)
  :ret ::s.a.admin-rates/index-response)
