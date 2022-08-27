(ns dinsro.actions.ln.nodes-lj
  (:refer-clojure :exclude [next])
  (:require
   [clojure.core.async :as async]
   [clojure.java.io :as io]
   [clojure.set :as set]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.info :as m.ln.info]
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

(>defn update-info!
  [{::m.ln.nodes/keys [id] :as node}]
  [::m.ln.nodes/item => any?]
  (log/info :update-info!/starting {:id id})
  (with-open [client (get-client node)]
    (let [ch (async/chan)]
      (.getInfo client (c.lnd/ch-observer ch))
      (async/go
        (let [response (async/<! ch)
              params   (set/rename-keys response m.ln.info/rename-map)]
          (log/info
           :update-info!/saving
           {:id       id
            :response response
            :params   params})
          (a.ln.nodes/save-info! id params)))
      ch)))
