(ns dinsro.actions.nbxplorer
  (:require
   [clojure.data.json :as json]
   [http.async.client :as http-client]))

(defn get-utxos
  [derivation-scheme]
  (let [base "http://nbxplorer.nbxplorer-alice:32838"
        path (str base "/v1/cryptos/BTC/derivations/" derivation-scheme "/utxos")]
    (with-open [client (http-client/create-client)]
      (let [response (http-client/GET client path)
            r2       (http-client/await response)
            r3       (http-client/string r2)]
        (json/read-str r3 :key-fn keyword)))))

(defn get-transactions
  [derivation-scheme]
  (let [base "http://nbxplorer.nbxplorer-alice:32838"
        path (str base "/v1/cryptos/BTC/derivations/" derivation-scheme "/transactions")]
    (with-open [client (http-client/create-client)]
      (let [response (http-client/GET client path)
            r2       (http-client/await response)
            r3       (http-client/string r2)]
        (json/read-str r3 :key-fn keyword)))))

(defn get-utxos-for-address
  [address]
  (let [base "http://nbxplorer.nbxplorer-alice:32838"
        path (str base "/v1/cryptos/BTC/addresses/" address "/utxos")]
    (with-open [client (http-client/create-client)]
      (let [response (http-client/GET client path)
            r2       (http-client/await response)
            r3       (http-client/string r2)]
        (json/read-str r3 :key-fn keyword)))))

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

(defn get-status
  []
  (let [base "http://nbxplorer.nbxplorer-alice:32838"
        path (str base "/v1/cryptos/BTC/status")]
    (with-open [client (http-client/create-client)]
      (let [response (http-client/GET client path)
            r2       (http-client/await response)
            r3       (http-client/string r2)]
        (json/read-str r3 :key-fn keyword)))))

(comment
  (def address "bcrt1ql0l7rwl5v3yhh0emgtcj5cxm8ssgveyr8y8pag")

  (def descriptor "wpkh([7c6cf2c1/84h/1h/0h]tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc/0/*)#8phlkw5l")

  (get-utxos "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc")
  (get-transactions "tpubDDV8TbjuWeytsM7mAwTTkwVqWvmZ6TpMj1qQ8xNmNe6fZcZPwf1nDocKoYSF4vjM1XAoVdie8avWzE8hTpt8pgsCosTdAjnweSy7bR1kAwc")
  (get-transactions "vpub5ZSyL2aPW1wUtxYg42u88qyTqsP1DqLJMACfDcXrGib1i8GEN7qZqEdDX6SHYcUGQLQHP71egAPNXUQxdaCsLn3i1bKUKPVjX3FR4Cj7Hgp")

  (track-address address)
  (:transactions (:confirmedTransactions (get-transactions-for-address address)))

  (get-status)

  nil)
