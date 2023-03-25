(ns dinsro.actions.nbxplorer
  (:require
   [clojure.data.json :as json]
   [http.async.client :as http-client]))

(defn get-transactions-for-address
  [address]
  (let [base "http://nbxplorer.nbxplorer-alice:32838"
        path (str base "/v1/cryptos/BTC/addresses/" address "/transactions")]
    (with-open [client (http-client/create-client)]
      (let [response (http-client/GET client path)
            r2       (http-client/await response)
            r3       (http-client/string r2)]
        (json/read-str r3 :key-fn keyword)))))

(defn track-address
  [address]
  (let [base "http://nbxplorer.nbxplorer-alice:32838"
        path (str base "/v1/cryptos/BTC/addresses/" address)]
    (with-open [client (http-client/create-client)]
      (let [response (http-client/POST client path :body "")]
        (when-let [r2 (http-client/await response)]
          (when-let [r3 (http-client/string r2)]
            (json/read-str r3 :key-fn keyword)))))))
