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
   [http.async.client :as http-client]
   [jq.api :as jq]
   [manifold.time :as t]
   [mount.core :as mount]
   [taoensso.timbre :as log]
   [tick.alpha.api :as tick]))

(declare ^:dynamic *scheduler*)

(defn run-query!
  [id]
  (log/infof "Running rate source: %s" id)
  (let [query "100000000 / (.data.amount | tonumber)"
        data "{\"data\":{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"61843.51\"}}"
        processor-fn (jq/processor query)
        rate (processor-fn data)
        params {::m.rates/rate   (Double/parseDouble rate)
                ::m.rates/source id
                ::m.rates/date   (tick/instant)}]
    (q.rates/create-record params)))

(comment

  (json/read-str (run-query! 1))

  nil)

;; TODO: handle request failures and backoff
(>defn fetch-rate
  [item]
  [::m.rate-sources/item => (? ::ds/valid-double)]
  (with-open [client (http-client/create-client)]
    (let [{::m.rate-sources/keys [url path]} item
          response (http-client/GET client url)
          body     (some-> response
                           http-client/await
                           http-client/string)]
      (Double/parseDouble (jq/execute body path)))))

(>defn fetch-source
  [{::m.rate-sources/keys [id] :as source}]
  [::m.rate-sources/item => ::m.currencies/id]
  (log/infof "Fetching source: %s" (::m.rate-sources/id source))
  (if-let [currency-id (some-> source ::m.rate-sources/currency)]
    (if-let [currency (q.currencies/read-record currency-id)]
      (if-let [rate (fetch-rate source)]
        (let [rate-item {::m.rates/source id
                         ::m.rates/rate     rate
                         ::m.rates/date     (tick/instant)}]
          (log/infof "Updating rate for currency %s => %s" (::m.currencies/name currency) rate)
          (q.rates/create-record rate-item))
        (log/error "No rate"))
      (log/error "Couldn't find currency"))
    (log/error "No Currency id")))

(defn check-rates
  []
  (log/info "Checking rates")
  (doseq [item (q.rate-sources/index-records)]
    (let [{::m.rate-sources/keys [active?]} item]
      (if active?
        (fetch-source item)
        (log/warnf "not active: %s" (::m.rate-sources/name item))))))

(defn stop-scheduler
  []
  (log/info "stopping")
  (*scheduler*)
  nil)

(defn start-scheduler
  []
  (log/info "starting scheduler")
  (t/every (t/minutes 5) #'check-rates))

(mount/defstate ^:dynamic *scheduler*
  :start (start-scheduler)
  :stop (stop-scheduler))

(comment

  *scheduler*

  (start-scheduler)
  (stop-scheduler)

  nil)
