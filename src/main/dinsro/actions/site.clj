(ns dinsro.actions.site
  (:require
   [clojure.java.io :as io]
   [clojure.edn :as edn]
   [clojure.string :as string]
   [dinsro.model.core-nodes :as m.core-nodes]
   [dinsro.queries.core-nodes :as q.core-nodes]
   [dinsro.queries.core-block :as q.core-block]
   [dinsro.queries.core-tx :as q.core-tx]
   [ring.util.codec :as codec]))

(defn load-edn
  "Load edn from an io/reader source (filename or io/resource)."
  [source]
  (try
    (with-open [r (io/reader source)]
      (edn/read (java.io.PushbackReader. r)))
    (catch java.io.IOException _e
      #_(printf "Couldn't open '%s': %s\n" source (.getMessage e))
      nil)
    (catch RuntimeException _e
      #_(printf "Error parsing edn file '%s': %s\n" source (.getMessage e))
      nil)))

(defn parse-lnurl
  [url]
  (let [[_ host port args] (re-matches #"^lndconnect://(?<host>[^:]+):(?<port>[\d]+)\?(?<args>.*)$" url)
        {macaroon "macaroon"
         cert     "cert"}  (codec/form-decode args)]
    {:host host :port port :macaroon macaroon :cert cert}))

(defn get-lnuris
  [site-config]
  (first (get-in site-config [:seeds :core-nodes :lnd-uris])))

(defn get-node-config
  [site-config]
  (first (get-in site-config [:seeds :core-nodes :nodes])))

(defn create-core-nodes
  [site-config]
  (let [{:keys [name host port rpcuser rpcpass]} (get-node-config site-config)]
    (q.core-nodes/create-record
     {::m.core-nodes/name    name
      ::m.core-nodes/host    host
      ::m.core-nodes/port    port
      ::m.core-nodes/rpcuser rpcuser
      ::m.core-nodes/rpcpass rpcpass})))

(comment
  (edn/read-string (slurp (io/file "site.edn")))

  (slurp (io/file "site.edn"))

  (tap> (first (get-in (load-edn "site.edn") [:seeds :core-nodes :lnd-uris])))

  (def ln-url (first (get-in (load-edn "site.edn") [:seeds :core-nodes :lnd-uris])))
  ln-url
  (tap> ln-url)

  (get-lnuris (load-edn "site.edn"))

  (first (get-in (load-edn "site.edn") [:seeds :core-nodes :nodes]))

  (q.core-nodes/index-records)

  (doseq [id (q.core-nodes/index-ids)]
    (q.core-nodes/delete! id))

  (doseq [id (q.core-block/index-ids)]
    (q.core-block/delete id))

  (doseq [id (q.core-tx/index-ids)]
    (q.core-tx/delete id))

  (create-core-nodes (load-edn "site.edn"))

  (tap> (parse-lnurl ln-url))

  (first (string/split ln-url #"\?"))
  (tap> (codec/form-decode (second (string/split ln-url #"\?"))))

  nil)
