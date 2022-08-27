(ns dinsro.actions.ln.nodes-lj
  (:refer-clojure :exclude [next])
  (:require
   [clojure.core.async :as async]
   [clojure.java.io :as io]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import
   io.grpc.stub.StreamObserver
   org.lightningj.lnd.wrapper.AsynchronousLndAPI))

(defn balance-observer
  [next]
  (reify StreamObserver
    (onNext [_this note] (next note))
    (onError [_this err] (println err))
    (onCompleted [_this] (println "onCompleted server"))))

(>defn get-client
  "Get a lightningj client"
  [{::m.ln.nodes/keys [id name host port]}]
  [::m.ln.nodes/item => (ds/instance? AsynchronousLndAPI)]
  (log/finer :get-client/starting {:id id :name name})
  (let [port-num      (Integer/parseInt port)
        cert-file     (m.ln.nodes/cert-file id)
        macaroon-file (io/file (m.ln.nodes/macaroon-path id))]
    (c.lnd/get-client host port-num cert-file macaroon-file)))

(>defn get-lnd-address
  [node]
  [::m.ln.nodes/item => any?]
  (with-open [client (get-client node)]
    (let [ch      (async/chan)
          request (c.lnd/->new-address-request)]
      (.newAddress client request (c.lnd/ch-observer ch))
      ch)))
