(ns dinsro.actions.ln.payreqs
  (:require
   [clojure.core.async :as async :refer [<!!]]
   [com.fulcrologic.guardrails.core :refer [>defn =>]]
   [dinsro.actions.ln.nodes :as a.ln.nodes]
   [dinsro.client.lnd :as c.lnd]
   [dinsro.model.ln.nodes :as m.ln.nodes]
   [dinsro.model.ln.payreqs :as m.ln.payreqs]
   [dinsro.queries.ln.nodes :as q.ln.nodes]
   [dinsro.queries.ln.payreqs :as q.ln.payreqs]
   [dinsro.queries.users :as q.users]
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
  (with-open [client (a.ln.nodes/get-client node)]
    (<!! (c.lnd/decode-pay-req client payment-request))))

(defn send-payment!
  [node payment-request]
  (with-open [client (a.ln.nodes/get-client node)]
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

(comment
  (def node-alice (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "alice") "lnd-alice")))
  (def node-bob (q.ln.nodes/read-record (q.ln.nodes/find-id-by-user-and-name (q.users/find-eid-by-name "bob") "lnd-bob")))
  (def node node-alice)
  node-alice
  node-bob
  node

  (q.ln.payreqs/index-ids)

  (map q.ln.payreqs/delete!   (q.ln.payreqs/index-ids))

  (def payment-request "lnbcrt230n1psuca02pp5lhjau56zdmmce9hva726lvqyxuztfsusyv7yw770mur5hu2x989qdq523jhxapqf9h8vmmfvdjscqzpgxqyz5vqsp5rxljvwf9r3q4zhecwr2ccnn3eakvs0n500t4p3hnrv626s8852ms9qyyssqpz8a9dnmhhk3q3ly8ykdnr3ts9m94v9e9un2j6rsvstqlhh0wdgrx84hawf2zagqs7ph24r4pqk0yy7jpmxay8f0kjxf6khkh2z9axspc25re5")
  (decode {::m.ln.payreqs/payment-request payment-request
           ::m.ln.payreqs/node            node-alice})

  nil)
