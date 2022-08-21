(ns dinsro.actions.ln.payreqs-lj
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.nodes-lj :as a.ln.nodes-lj]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.payreqs :as q.ln.payreqs]
   [taoensso.timbre :as log])
  (:import
   org.lightningj.lnd.wrapper.message.ListPaymentsRequest))

(defn list-payment-request
  []
  (let [request (ListPaymentsRequest.)]
    request))

(>defn decode-pay-req
  [node payment-request]
  [::m.ln.nodes/item string? => any?]
  (with-open [client (a.ln.nodes-lj/get-client node)]
    (<!! (c.lnd/decode-pay-req client payment-request))))

(defn send-payment!
  [node payment-request]
  (with-open [client (a.ln.nodes-lj/get-client node)]
    (<!! (c.lnd/send-payment-sync client payment-request))))

(defn submit!
  [props]
  (log/infof "Submitting: %s" props)
  (let [{::m.ln.payreqs/keys       [payment-request]
         {node-id ::m.ln.nodes/id} ::m.ln.payreqs/node} props]
    (if-let [node (q.ln.nodes/read-record node-id)]
      (send-payment! node payment-request)
      {:status :not-found})))

(defn decode
  [props]
  (log/infof "Decoding request: %s" props)
  (let [{::m.ln.payreqs/keys      [payment-request]
         {node-id ::m.ln.nodes/id} ::m.ln.payreqs/node} props]
    (if-let [node (q.ln.nodes/read-record node-id)]
      (let [params (decode-pay-req node payment-request)
            params (assoc params ::m.ln.payreqs/node node-id)
            params (assoc params ::m.ln.payreqs/payment-request payment-request)
            params (m.ln.payreqs/prepare-params params)
            id     (q.ln.payreqs/create-record params)]
        {:status :success
         :id     id})
      {:status :not-found})))
