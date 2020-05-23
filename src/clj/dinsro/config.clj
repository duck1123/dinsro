(ns dinsro.config
  (:require
   [buddy.core.nonce :refer [random-bytes]]
   [cprop.core :refer [load-config]]
   [cprop.source :as source]
   [mount.core :refer [args defstate]]
   [ring.util.codec :refer [base64-encode base64-decode]]
   [taoensso.timbre :as timbre])
  (:import java.io.FileNotFoundException))

(defstate env
  :start
  (load-config
    :merge
    [(args)
     (source/from-system-props)
     (source/from-env)]))

(defn generate-secret
  []
  (random-bytes 32))

(defn write-secret
  []
  (spit ".secret" (base64-encode (generate-secret))))

(defn read-secret
  []
  (try (base64-decode (slurp ".secret"))
       (catch FileNotFoundException ex
         (timbre/warn "No secret found"))))

(defstate secret
  :start
  (or (env :secret)
      (if-let [stored-secret (read-secret)]
        stored-secret
        (do
          (write-secret)
          (read-secret)))))
