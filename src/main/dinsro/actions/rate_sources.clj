(ns dinsro.actions.rate-sources
  (:require
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [expound.alpha :as expound]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [dinsro.specs.actions.rate-sources :as s.a.rate-sources]
   [dinsro.utils :as utils]
   [http.async.client :as http-client]
   [manifold.time :as t]
   [mount.core :as mount]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(declare ^:dynamic *scheduler*)

;; Prepare

(defn prepare-record
  [params]
  (let [params {::m.rate-sources/currency {:db/id (:currency-id params)}
                ::m.rate-sources/name (some-> params :name)
                ::m.rate-sources/url (some-> params :url)}]
    (if (s/valid? ::m.rate-sources/params params)
      params
      (do
        (comment (timbre/debugf "not valid: %s" (expound/expound-str ::m.rate-sources/params params)))
        nil))))

(s/fdef prepare-record
  :args (s/cat :params ::s.a.rate-sources/create-params)
  :ret  (s/nilable ::m.rate-sources/params))

;; Create

(defn create-handler
  [request]
  (or (let [{params :params} request]
        (when-let [params (prepare-record params)]
          (when-let [id (q.rate-sources/create-record params)]
            (http/ok {:item (q.rate-sources/read-record id)}))))
      (http/bad-request {:status :invalid})))

(s/fdef create-handler
  :args (s/cat :request ::s.a.rate-sources/create-request)
  :ret ::s.a.rate-sources/create-response)

;; Index

(defn index-handler
  [_]
  (let [;; TODO: parse from request
        limit 50
        items (q.rate-sources/index-records)
        response {:model :rate-sources
                  :limit limit
                  :items items}]
    (http/ok response)))

(s/fdef index-handler
  :args (s/cat :request ::s.a.rate-sources/index-request)
  :ret ::s.a.rate-sources/index-response)

;; Read

(defn read-handler
  [request]
  (if-let [id (some-> (get-in request [:path-params :id]) utils/try-parse-int)]
    (if-let [item (q.rate-sources/read-record id)]
      (http/ok {:item item})
      (http/not-found {:status :not-found}))
    (http/bad-request {:status :bad-request})))

(s/fdef read-handler
  :args (s/cat :request ::s.a.rate-sources/read-request)
  :ret ::s.a.rate-sources/read-response)

;; Delete

(defn delete-handler
  [request]
  (let [id (Integer/parseInt (get-in request [:path-params :id]))]
    (q.rate-sources/delete-record id)
    (http/ok {:id id})))

(s/fdef delete-handler
  :args (s/cat :request ::s.a.rate-sources/delete-request)
  :ret ::s.a.rate-sources/delete-response)

;; TODO: handle request failures and backoff
(defn fetch-rate
  [item]
  (with-open [client (http-client/create-client)]
    (let [url (::m.rate-sources/url item)
          response (http-client/GET client url)
          body (some-> response
                       http-client/await
                       http-client/string
                       (json/read-str :key-fn keyword))]
      (when-let [price (some-> body :price utils/parse-double)]
        (/ 100000000 price)))))

(s/fdef fetch-rate
  :args (s/cat :item ::m.rate-sources/item)
  :ret (s/nilable ::ds/valid-double))

(defn fetch-source
  [item]
  (if-let [currency-id (some-> item ::m.rate-sources/currency :db/id)]
    (if-let [currency (q.currencies/read-record currency-id)]
      (if-let [rate (fetch-rate item)]
        (let [rate-item {::m.rates/currency {:db/id currency-id}
                         ::m.rates/rate rate
                         ::m.rates/date (tick/instant)}]
          (timbre/infof "Updating rate for currency %s => %s" (::m.currencies/name currency) rate)
          (q.rates/create-record rate-item))
        (timbre/error "No rate"))
      (timbre/error "Couldn't find currency"))
    (timbre/error "No Currency id")))

(s/fdef fetch-source
  :args (s/cat :item ::m.rate-sources/item)
  :ret :db/id)

(defn run-handler
  [request]
  (let [id (some-> (get-in request [:path-params :id]) utils/try-parse-int)]
    (if-let [item (q.rate-sources/read-record id)]
      (try
        (let [id (fetch-source item)
              rate (q.rates/read-record id)]
          (http/ok {:status :ok :item rate}))
        (catch NumberFormatException e
          (http/internal-server-error {:status :error :message (.getMessage e)})))
      (http/not-found {:status :not-found}))))

(defn check-rates
  []
  (doseq [item (q.rate-sources/index-records)]
    (fetch-source item)))

(defn stop-scheduler
  []
  (timbre/info "stopping")
  (*scheduler*)
  nil)

(defn start-scheduler
  []
  (timbre/info "starting")
  (t/every
   (t/minutes 5)
   (t/seconds 30)
   #'check-rates))

(mount/defstate ^:dynamic *scheduler*
  :start (start-scheduler)
  :stop (stop-scheduler))
