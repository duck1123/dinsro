(ns dinsro.actions.rate-sources
  (:require
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.model.currencies :as m.currencies]
   [dinsro.model.rate-sources :as m.rate-sources]
   [dinsro.model.rates :as m.rates]
   [dinsro.queries.currencies :as q.currencies]
   [dinsro.queries.rate-sources :as q.rate-sources]
   [dinsro.queries.rates :as q.rates]
   [dinsro.specs :as ds]
   [http.async.client :as http-client]
   [jq.api :as jq]
   [lambdaisland.glogc :as log]
   [manifold.time :as t]
   [mount.core :as mount]
   [tick.alpha.api :as tick]))

(declare ^:dynamic *scheduler*)

(defn run-query!
  [id]
  (log/info :source/running {:id id})
  (let [query "100000000 / (.data.amount | tonumber)"
        data "{\"data\":{\"base\":\"BTC\",\"currency\":\"USD\",\"amount\":\"61843.51\"}}"
        processor-fn (jq/processor query)
        rate (processor-fn data)
        params {::m.rates/rate   (Double/parseDouble rate)
                ::m.rates/source id
                ::m.rates/date   (tick/instant)}]
    (q.rates/create-record params)))

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
  (log/info :source/fetching {:source-id id})
  (if-let [currency-id (some-> source ::m.rate-sources/currency)]
    (if-let [currency (q.currencies/read-record currency-id)]
      (if-let [rate (fetch-rate source)]
        (let [rate-item {::m.rates/source id
                         ::m.rates/rate     rate
                         ::m.rates/date     (tick/instant)}]
          (log/info :rate/updating {:currency currency :rate rate})
          (q.rates/create-record rate-item))
        (log/error :rate/not-found {:source source}))
      (log/error :currency/not-found {:currency-id currency-id}))
    (log/error :currency/missing-id {})))

(defn check-rates
  []
  (log/info :rate-checking/started {})
  (doseq [item (q.rate-sources/index-records)]
    (let [{::m.rate-sources/keys [active?]} item]
      (if active?
        (fetch-source item)
        (log/warn :rate/not-active {:source-id (::m.rate-sources/id item)})))))

(defn stop-scheduler
  []
  (log/info :scheduler/stopping {})
  (when *scheduler* (*scheduler*))
  nil)

(def scheduler-enabled false)

(defn start-scheduler
  []
  (log/info :scheduler/starting {:enabled scheduler-enabled})
  (when scheduler-enabled
    (t/every (t/minutes 5) #'check-rates)))

(mount/defstate ^:dynamic *scheduler*
  :start (start-scheduler)
  :stop (stop-scheduler))

(comment

  (json/read-str (run-query! 1))

  *scheduler*

  (start-scheduler)
  (stop-scheduler)
  (check-rates)

  nil)
