(ns dinsro.components.secrets
  (:require
   [buddy.core.nonce :refer [random-bytes]]
   [dinsro.components.config :as config]
   [mount.core :refer [defstate]]
   [ring.util.codec :refer [base64-encode base64-decode]]
   [taoensso.timbre :as timbre])
  (:import java.io.FileNotFoundException))

(defn generate-secret
  []
  (random-bytes 32))

(defn write-secret
  []
  (spit ".secret" (base64-encode (generate-secret))))

(defn read-secret
  []
  (try (base64-decode (slurp ".secret"))
       (catch FileNotFoundException _ex
         (timbre/warn "No secret found"))))

(defstate secret
  :start
  (or (:secret config/config)
      (if-let [stored-secret (read-secret)]
        stored-secret
        (do
          (write-secret)
          (read-secret)))))
