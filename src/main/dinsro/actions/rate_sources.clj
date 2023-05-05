(ns dinsro.actions.rate-sources
  (:require
   [clojure.data.csv :as csv]
   [clojure.data.json :as json]
   [clojure.java.io :as io]
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
   [manifold.time :as mt]
   [mount.core :as mount]
   [tick.alpha.api :as t]))

;; [[../queries/rate_sources.clj]]
;; [[../processors/rate_sources.clj]]

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
                ::m.rates/date   (t/instant)}]
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
                         ::m.rates/date     (t/instant)}]
          (log/info :rate/updating {:currency currency :rate rate})
          (q.rates/create-record rate-item))
        (log/error :rate/not-found {:source source}))
      (log/error :currency/not-found {:currency-id currency-id}))
    (log/error :currency/missing-id {})))

(defn check-rates
  []
  (log/info :rate-checking/started {})
  (doseq [id (q.rate-sources/index-ids)]
    (let [item (q.rate-sources/read-record id)
          {::m.rate-sources/keys [active?]} item]
      (if active?
        (fetch-source item)
        (log/warn :rate/not-active {:source-id (::m.rate-sources/id item)})))))

(def scheduler-enabled false)

(defn start-scheduler!
  []
  (log/info :start-scheduler!/starting {:enabled scheduler-enabled})
  (let [scheduler (when scheduler-enabled (mt/every (mt/minutes 5) #'check-rates))]
    (log/info :start-scheduler!/finished {:scheduler scheduler})
    scheduler))

(defn stop-scheduler!
  []
  (log/info :stop-scheduler!/starting {})
  (when-let [stop! @*scheduler*]
    (stop!))
  (log/info :stop-scheduler!/finished {})
  nil)

(mount/defstate ^:dynamic *scheduler*
  :start (start-scheduler!)
  :stop (stop-scheduler!))

(defn read-csv
  [filename]
  (with-open [reader (io/reader filename)]
    (->> (csv/read-csv reader)
         (drop 2)
         (mapv (fn [[ts date symbol open high low close volume]]
                 {:ts     ts
                  :date   date
                  :symbol symbol
                  :open   open
                  :high   high
                  :low    low
                  :close  close
                  :volume volume})))))

(defn load-rates!
  []
  (log/info :load-rates!/starting {}))

(defn create!
  [props]
  (log/info :create!/starting {:props props})
  nil)

(>defn delete!
  [rate-source-id]
  [::m.rate-sources/id => nil?]
  (log/info :delete!/starting {:rate-source-id rate-source-id})
  (q.rate-sources/delete! rate-source-id)
  (log/info :delete!/finished {:rate-source-id rate-source-id})
  nil)

(comment

  (def path "resources/rates/gemini_BTCUSD_day.csv")
  (def file (io/as-file path))

  (.getAbsolutePath file)
  (.exists file)

  (read-csv path)

  (json/read-str (run-query! 1))

  *scheduler*

  (q.rate-sources/index-ids)

  (start-scheduler!)
  (stop-scheduler!)
  (check-rates)

  nil)
