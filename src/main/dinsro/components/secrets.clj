(ns dinsro.components.secrets
  (:require
   [buddy.core.nonce :refer [random-bytes]]
   [dinsro.components.config :as config]
   [lambdaisland.glogc :as log]
   [mount.core :refer [defstate]]
   [ring.util.codec :refer [base64-encode base64-decode]])
  (:import java.io.FileNotFoundException))

(def secret-filename ".secret")

(defn generate-secret
  "Create a new random secret"
  []
  (random-bytes 32))

(defn write-secret
  "Generate a new secret and write it to a file"
  []
  (spit secret-filename (base64-encode (generate-secret))))

(defn read-secret
  "Read the secret file"
  []
  (try
    (base64-decode (slurp secret-filename))
    (catch FileNotFoundException _ex
      (log/warn :read-secret/no-secret {}))))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defstate secret
  "A secret value used to encrypt session state"
  :start
  (do
    (log/info :secret/starting {})
    (or (:secret (config/get-config))
        (if-let [stored-secret (read-secret)]
          stored-secret
          (do
            (write-secret)
            (read-secret))))))
