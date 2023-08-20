(ns dinsro.processors.nostr.events
  (:require
   [clojure.core.async :as async]
   [dinsro.actions.nostr.events :as a.n.events]
   [dinsro.actions.nostr.filter-items :as a.n.filter-items]
   [dinsro.actions.nostr.filters :as a.n.filters]
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.actions.nostr.runs :as a.n.runs]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.responses.nostr.events :as r.n.events]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/events.clj]]
;; [[../../model/nostr/events.cljc]]
;; [[../../responses/nostr/events.cljc]]
;; [[../../ui/nostr/events.cljc]]

(def model-key ::m.n.events/id)

(defn fetch!
  [props]
  (log/info :fetch!/starting {:props props})
  (let [id (model-key props)]
    (a.n.events/fetch-event! id)))

(defn create-event-request!
  [pubkey-id relay-id]
  (log/info :create-event-request!/starting {:pubkey-id pubkey-id :relay-id relay-id})
  (let [request-id (a.n.requests/register-request relay-id)
        filter-id  (a.n.filters/register-filter! request-id)]
    (a.n.filter-items/register-pubkey! filter-id pubkey-id)
    (a.n.filter-items/register-kind! filter-id 1)
    request-id))

(defn fetch-events!
  [props]
  (log/info :fetch-events!/starting {:props props})
  (let [{pubkey-id ::m.n.pubkeys/id relay-id ::m.n.relays/id} props]
    (log/info :fetch-events!/starting {:pubkey-id pubkey-id :relay-id relay-id})
    (let [request-id (create-event-request! pubkey-id relay-id)
          run-id     (a.n.runs/create-run! request-id)
          ch         (a.n.runs/start! run-id)]
      (async/go-loop []
        (if-let [response (async/<! ch)]
          (let [{:keys [content]} response]
            (log/info :fetch-events/received {:content content})
            (recur))
          (do (log/info :fetch-events!/no-message {})
              nil))))

    (try
      {::mu/status :ok}
      (catch Exception ex
        (mu/exception-response ex)))))

(defn delete!
  [_env props]
  (let [id (model-key props)]
    (a.n.events/delete! id)
    {::mu/status                  :ok
     ::r.n.events/deleted-records (m.n.events/idents [id])}))
