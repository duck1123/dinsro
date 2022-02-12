(ns dinsro.components.secrets
  (:require
   [buddy.core.nonce :refer [random-bytes]]
   [dinsro.components.config :as config]
   [mount.core :refer [defstate]]
   [ring.util.codec :refer [base64-encode base64-decode]]
   [taoensso.timbre :as log])
  (:import java.io.FileNotFoundException))

(defn generate-secret
  "Create a new random secret"
  []
  (random-bytes 32))

(defn write-secret
  "Generate a new secret and write it to a file"
  []
  (spit ".secret" (base64-encode (generate-secret))))

(defn read-secret
  "Read the secret file"
  []
  (try (base64-decode (slurp ".secret"))
       (catch FileNotFoundException _ex
         (log/warn "No secret found"))))

(defstate secret
  "A secret value used to encrypt session state"
  :start
  (or (:secret config/config)
      (if-let [stored-secret (read-secret)]
        stored-secret
        (do
          (write-secret)
          (read-secret)))))
