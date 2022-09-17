(ns dinsro.actions.ln.nodes
  (:refer-clojure :exclude [next])
  (:require
   [buddy.core.codecs :refer [bytes->hex]]
   [clj-commons.byte-streams :as bs]
   [clojure.core.async :as async]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn => ?]]
   [dinsro.actions.core.nodes :as a.c.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.client.lnd-s :as c.lnd-s]
   [dinsro.client.scala :as cs]
   [dinsro.model.ln.info :as m.ln.info]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.queries.core.nodes :as q.c.nodes]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.specs :as ds]
   [erp12.fijit.collection :as efc]
   [lambdaisland.glogc :as log])
  (:import
   java.io.File
   java.io.FileNotFoundException
   java.net.UnknownHostException
   java.net.URI
   org.bitcoins.lnd.rpc.LndRpcClient
   scala.Option))

(def default-passphrase "password12345678")

(>defn get-cert-text
  [node]
  [::m.ln.nodes/item => (? string?)]
  (try
    (slurp (m.ln.nodes/cert-file (::m.ln.nodes/id node)))
    (catch FileNotFoundException _ nil)))

(>defn get-macaroon-hex
  [node]
  [::m.ln.nodes/item => (? string?)]
  (try
    (let [f (m.ln.nodes/macaroon-file (::m.ln.nodes/id node))]
      (bytes->hex (bs/to-byte-array f)))
    (catch FileNotFoundException _ nil)))

(defn get-client
  "Get a bitcoin-s client for this node"
  ^LndRpcClient [{::m.ln.nodes/keys [id name host port] :as node}]
  (log/info :get-client/creating {:id id :name name})
  (let [url       (URI. (str "https://" host ":" port "/"))
        cert-file (Option/apply (get-cert-text node))
        macaroon  (or (get-macaroon-hex node) "")]
    (if macaroon
      (let [instance (c.lnd-s/get-remote-instance url macaroon (Option/empty) cert-file)]
        (log/finer :get-client/creating {:url url :cert-file cert-file :macaroon macaroon})
        (c.lnd-s/get-remote-client instance))
      (throw (RuntimeException. "No macaroon")))))

(>defn download-file
  "Download a file from a remote uri"
  [uri file]
  [string? (partial instance? File) => boolean?]
  (try
    (with-open [in  (io/input-stream uri)
                out (io/output-stream file)]
      (io/copy in out))
    true
    (catch UnknownHostException _ex
      (log/warn :download-file/unknown-host {:uri uri :file file})
      false)))

(>defn has-cert?
  "Is the cert available for this node?"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => boolean?]
  (log/info :cert/checking {:node-id id})
  (m.ln.nodes/has-cert? id))

(>defn has-macaroon?
  "Is the macaroon available for this node?"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => boolean?]
  (log/info :macaroon/checking {:node-id id})
  (m.ln.nodes/has-macaroon? id))

(>defn delete-cert
  "Delete the cert file"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => nil?]
  (let [path (m.ln.nodes/cert-path id)
        f    (io/file path)]
    (.delete f)))

(>defn delete-macaroon
  "Delete the macaroon file"
  [{::m.ln.nodes/keys [id]}]
  [::m.ln.nodes/item => nil?]
  (let [path (m.ln.nodes/macaroon-path id)
        f    (io/file path)]
    (.delete f)))

(>defn download-cert!
  "Download the cert from the fileserver"
  [node]
  [::m.ln.nodes/item => boolean?]
  (let [node-id (::m.ln.nodes/id node)]
    (log/info :download-cert!/starting {:node-id node-id})
    (let [fileserver-host (::m.ln.nodes/fileserver-host node)
          dir-path        (m.ln.nodes/data-path node-id)
          dir-file        (io/file dir-path)
          url             (format "http://%s/tls.cert" fileserver-host)]
      (log/info
       :download-cert!/downloading
       {:node-id  node-id
        :dir-path dir-path
        :dir-file dir-file
        :url      url})
      (.mkdirs dir-file)
      (let [cert-file (m.ln.nodes/cert-file node-id)]
        (if-let [response (download-file url cert-file)]
          (do
            (log/info :download-cert!/downloaded {:response response})
            response)
          (throw (RuntimeException. "Failed to download cert")))))))

(>defn download-macaroon!
  "Download the macaroon from the fileserver"
  [{::m.ln.nodes/keys [id fileserver-host]}]
  [::m.ln.nodes/item => (? (ds/instance? File))]
  (let [url      (format "http://%s/admin.macaroon" fileserver-host)
        dir-path (m.ln.nodes/data-path id)
        file     (io/file (m.ln.nodes/macaroon-path id))]
    (log/info
     :download-macaroon!/starting
     {:id id :file file :url url :dir-path dir-path})
    (.mkdirs (io/file dir-path))
    (try
      (if (download-file url file)
        file
        (do
          (log/error :download-macaroon!/failed {:node-id id :fileserver-host fileserver-host})
          nil))
      (catch FileNotFoundException _ex
        (log/error :download-macaroon!/download-failed {:url url})
        nil))))

(defn get-macaroon-text
  [node]
  (slurp (download-macaroon! node)))

(defn next-address
  [client]
  (let [ch      (async/chan)
        request (c.lnd/->addr-request "")]
    (.nextAddr client request (c.lnd/ch-observer ch))
    ch))

(defn initialize!
  [node]
  (log/info :initialize!/starting {:node node})
  (let [client   (get-client node)
        password "password12345678"]
    (if-let [mnemonic (::m.ln.nodes/mnemonic node)]
      (let [cipher-seed-mnemonic (efc/to-scala-list mnemonic)]
        (log/info :initialize!/mnemonic {:mnemonic cipher-seed-mnemonic})
        (try
          (c.lnd-s/initialize! client password cipher-seed-mnemonic)
          (catch Exception ex
            (log/error :initialize!/error {:ex ex})
            (throw ex))))
      (throw (RuntimeException. "no mnemonic")))))

;; (defn get-remote-instance
;;   "Create a Bitcoin-s lnd remote instance for node"
;;   ^LndInstance [{::m.ln.nodes/keys [host port] :as node}]
;;   (let [url       (URI. (str "http://" host ":" port "/"))
;;         macaroon  (get-macaroon-text node)]
;;     (c.lnd-s/get-remote-instance url macaroon)))

(defn get-info
  "Fetch info for the node"
  [node]
  (let [client (get-client node)
        response (c.lnd-s/get-info client)]
    (log/info :get-info/response {:response response})
    (let [record (cs/->record response)]
      (log/info :get-info/converted {:record record})
      record)))

(>defn new-address
  "Request a new address from the node"
  [node]
  [::m.ln.nodes/item => any?]
  (let [client (get-client node)]
    (c.lnd-s/get-new-address client)))

(defn new-address-str
  "Request new address as string"
  [node]
  (.value (new-address node)))

(defn unlock!
  "Unlock the wallet"
  [node]
  (let [node-id (::m.ln.nodes/id node)]
    (log/info :unlock!/starting {:node-id node-id})
    (let [client     (get-client node)
          passphrase default-passphrase]
      (try
        (let [response (c.lnd-s/unlock-wallet client passphrase)]
          (log/info :unlock!/finished {:node-id node-id :response response})
          response)
        (catch Exception ex
          (log/error :unlock!/failed {:message "Unlock failed" :ex ex})
          (throw ex))))))

(>defn get-lnd-address
  "Fetch an address from the node"
  [node]
  [::m.ln.nodes/item => string?]
  (log/info :get-lnd-address/starting {:node-id (::m.ln.nodes/id node)})
  (let [client     (get-client node)]
    (async/<!! (c.lnd-s/get-new-address client))))

(defn generate!
  "Request an address and generate a block to it"
  [node]
  [::m.ln.nodes/item => any?]
  (log/info :generate!/starting {:node-id (::m.ln.nodes/id node)})
  (let [{:keys [address]} (async/<!! (get-lnd-address node))
        cnode             (first (q.c.nodes/index-records))]
    (a.c.nodes/generate-to-address! cnode address)
    address))

(>defn update-info!
  "Fetch and update this node's info"
  [{::m.ln.nodes/keys [id] :as node}]
  [::m.ln.nodes/item => any?]
  (log/info :update-info!/starting {:id id})
  (let [client   (get-client node)
        response (c.lnd-s/get-info client)]
    (log/info :update-info!/got-channel {:response response})
    (let [record (cs/->record response)]
      (log/info :update-info!/converted {:record record})
      (let [params (set/rename-keys record m.ln.info/rename-map)]
        (log/finer :update-info!/saving {:id id :params params})
        (q.ln.nodes/update! id params)))))
