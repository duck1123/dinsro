(ns dinsro.actions.rate-sources
  (:require
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [dinsro.utils :as utils]
   [http.async.client :as http-client]
   [manifold.time :as t]
   [mount.core :as mount]
   [ring.util.http-response :as http]
   [taoensso.timbre :as timbre]
   [tick.alpha.api :as tick]))

(declare ^:dynamic *scheduler*)

;; TODO: handle request failures and backoff
(>defn fetch-rate
  [item]
  [::m.rate-sources/item => (? ::ds/valid-double)]
  (with-open [client (http-client/create-client)]
    (let [url      (::m.rate-sources/url item)
          response (http-client/GET client url)
          body     (some-> response
                           http-client/await
                           http-client/string
                           (json/read-str :key-fn keyword))]
      (when-let [price (some-> body :price utils/parse-double)]
        (/ 100000000 price)))))

(>defn fetch-source
  [item]
  [::m.rate-sources/item => ::m.currencies/id]
  (if-let [currency-id (some-> item ::m.rate-sources/currency ::m.currencies/id)]
    (if-let [currency (q.currencies/read-record currency-id)]
      (if-let [rate (fetch-rate item)]
        (let [rate-item {::m.rates/currency {::m.currencies/id currency-id}
                         ::m.rates/rate     rate
                         ::m.rates/date     (tick/instant)}]
          (timbre/infof "Updating rate for currency %s => %s" (::m.currencies/name currency) rate)
          (q.rates/create-record rate-item))
        (timbre/error "No rate"))
      (timbre/error "Couldn't find currency"))
    (timbre/error "No Currency id")))

(defn run-handler
  [request]
  (let [id (some-> (get-in request [:path-params :id]) utils/try-parse-int)]
    (if-let [item (q.rate-sources/read-record id)]
      (try
        (let [id   (fetch-source item)
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
