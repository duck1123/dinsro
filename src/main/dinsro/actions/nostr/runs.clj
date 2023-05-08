(ns dinsro.actions.nostr.runs
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.connections :as a.n.connections]
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.actions.nostr.witnesses :as a.n.witnesses]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.queries.nostr.connections :as q.n.connections]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.requests :as q.n.requests]
   [dinsro.queries.nostr.runs :as q.n.runs]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [../../model/nostr/runs.cljc]
;; [../../queries/nostr/runs.clj]

(defn register-run!
  [request-id connection-id]
  (log/info :register-run!/starting {:request-id request-id :connection-id connection-id})
  (if-let [run-id (q.n.runs/find-by-request-and-connection request-id connection-id)]
    (do
      (log/info :register-run!/found {:run-id run-id})
      run-id)
    (q.n.runs/create-record
     {::m.n.runs/request    request-id
      ::m.n.runs/connection connection-id})))

(>defn create-run!
  [request-id]
  [::m.n.requests/id => ::m.n.runs/id]
  (log/info :create-run!/starting {:request-id request-id})
  (let [relay-id      (q.n.relays/find-by-request request-id)
        connection-id (a.n.connections/register-connection! relay-id)
        run-id        (register-run! request-id connection-id)]
    run-id))

(>defn get-connection*
  ([run-id]
   [::m.n.runs/id => (? ::m.n.connections/id)]
   (get-connection* run-id true))
  ([run-id register]
   [::m.n.runs/id boolean? => (? ::m.n.connections/id)]
   (if-let [relay-id (q.n.relays/find-by-run run-id)]
     (if-let [connection-id (a.n.connections/register-connection!* relay-id register)]
       (do
         (log/info :get-connection/finished {:connection-id connection-id})
         connection-id)
       (do
         (log/info :get-connection/no-connection {:relay-id relay-id})
         nil))
     (throw (ex-info "No relay" {})))))

(>defn get-connection
  [run-id]
  [::m.n.runs/id => ::m.n.connections/id]
  (get-connection* run-id true))

(>defn start!
  [run-id]
  [::m.n.runs/id => ds/channel?]
  (log/info :start!/starting {:run-id run-id})
  (if-let [request-id (q.n.requests/find-by-run run-id)]
    (let [connection-id (get-connection run-id)
          query-string  (a.n.requests/get-query-string request-id)
          event-ch      (async/chan)]
      (a.n.connections/set-topic-channel run-id event-ch)
      (q.n.runs/set-started! run-id)
      (a.n.connections/send! connection-id query-string)
      (let [run-ch (async/chan)]
        (async/go-loop []
          (if-let [msg (async/<! event-ch)]
            (do
              (log/info :start!/received {:msg msg})
              (when (= (:type msg) "EVENT")
                (a.n.witnesses/witness! run-id msg)
                (async/>! run-ch msg))
              (when (= (:type msg) "EOSE")
                (q.n.runs/set-finished! run-id))
              (recur))
            (do
              (log/info :start!/closed {:run-id run-id})
              (async/close! run-ch)
              (q.n.runs/set-stopped! run-id)
              nil)))
        run-ch))
    (throw (ex-info "Failed to find request" {}))))

(defn stop!
  [run-id]
  (log/info :stop!/starting {:run-id run-id})
  (if-let [code (q.n.requests/find-code-by-run run-id)]
    (if-let [connection-id (get-connection* run-id false)]
      (do
        (if-let [chan (a.n.connections/get-topic-channel* run-id)]
          (do (log/info :stop!/stopping {:chan chan})
              (async/close! chan))
          (do
            (log/error :stop/no-channel {:connection-id connection-id})
            (a.n.connections/disconnect! connection-id)))
        (a.n.connections/send! connection-id (json/json-str ["CLOSE" code])))
      (do
        (log/error :stop!/no-connection {})
        (q.n.runs/set-stopped! run-id)))
    (throw (ex-info "no code" {}))))

(defn delete!
  [run-id]
  (log/info :delete!/starting {:run-id run-id})
  (throw (ex-info "Not Implemented" {})))

(comment

  (def run-id (first (q.n.runs/find-active)))
  run-id

  (stop! run-id)

  (q.n.connections/index-ids)
  (def connection-id (first (q.n.connections/find-connected)))
  connection-id

  (q.n.runs/find-by-connection connection-id)
  (q.n.runs/find-active-by-connection connection-id)

  (q.n.runs/find-active)

  (q.n.runs/read-record run-id)

  nil)
