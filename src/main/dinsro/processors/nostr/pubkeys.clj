(ns dinsro.processors.nostr.pubkeys
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
   [dinsro.actions.nostr.runs :as a.n.runs]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.mutations :as mu]
   [dinsro.options.contacts :as o.contacts]
   [dinsro.queries.contacts :as q.contacts]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.responses.nostr.pubkeys :as r.n.pubkeys]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../actions/nostr/pubkeys.clj]]
;; [[../../mutations/nostr/pubkeys.cljc]]
;; [[../../queries/contacts.clj]]
;; [[../../responses/nostr/pubkeys.cljc]]

(def model-key ::m.n.pubkeys/id)

(defn add-contact!
  [{:keys [query-params]} props]
  (let [pubkey-id (model-key props)
        actor-id  (:actor/id query-params)]
    (when actor-id
      (q.contacts/create!
       {o.contacts/pubkey pubkey-id
        o.contacts/user   actor-id}))
    {::mu/status :ok}))

(>defn update-pubkey!
  "Fetch the kind 0 information for a pubkey"
  [pubkey-id relay-id]
  [::m.n.pubkeys/id ::m.n.relays/id => ds/channel?]
  (log/info :update-pubkey!/starting {:pubkey-id pubkey-id :relay-id relay-id})
  (let [request-id (a.n.pubkeys/create-pubkey-update-request pubkey-id relay-id)
        run-id     (a.n.runs/create-run! request-id)]
    (log/info :update-pubkey!/running {:run-id run-id})
    (let [ch               (a.n.runs/start! run-id)
          response-channel (async/chan)]
      (async/go-loop []
        (if-let [response (async/<! ch)]
          (let [{:keys [tags pubkey content]} response]
            (log/info :update-pubkey!/received {:content content})
            (let [data                          (json/read-json content)
                  processed                     (a.n.pubkeys/process-pubkey-data! pubkey data tags)]
              (async/>! response-channel processed))
            (recur))
          (do (log/info :update-pubkey!/no-message {})
              (async/close! response-channel)
              nil)))
      response-channel)))

(>defn fetch!
  "Handler for fetch! mutation"
  [props]
  [::r.n.pubkeys/fetch!-request => ::r.n.pubkeys/fetch!-response]
  (let [{pubkey-id ::m.n.pubkeys/id relay-id ::m.n.relays/id} props]
    (log/info :fetch!/starting {:pubkey-id pubkey-id :relay-id relay-id})
    (try
      (if relay-id
        (if pubkey-id
          (let [response-ch (update-pubkey! pubkey-id relay-id)]
            (log/info :fetch!/finished {:response-ch response-ch})
            (let [response-channel (async/chan)]
              (async/go-loop []
                (if-let [pubkey-id (async/<! response-ch)]
                  (do
                    (log/info :fetch!/response-received {:pubkey-id pubkey-id})
                    (let [pubkey (q.n.pubkeys/read-record pubkey-id)]
                      (log/info :fetch!/response-read {:pubkey pubkey})
                      (async/>! response-channel pubkey)
                      (recur)))
                  (do
                    (log/info :fetch!/response-done {})
                    (async/close! response-channel)
                    nil)))
              (let [response-pubkey (async/<!! (a.n.relay-client/take-timeout response-channel))]
                (log/info :fetch!/pubkey-response {:response-pubkey response-pubkey})
                (if (= response-pubkey :timeout)
                  (mu/error-response "Timeout")
                  {::mu/status :ok ::m.n.pubkeys/item response-pubkey}))))

          (throw (ex-info "No pubkey" {})))
        (do
          (log/warn :fetch!/no-relay {:pubkey-id pubkey-id})
          (throw (ex-info "No relay" {}))))
      (catch Exception ex
        (log/error :fetch!/failed {:exception ex})
        (mu/exception-response ex)))))

(defn fetch-events!
  [props]
  (log/info :fetch-events!/starting {:props props}))

(defn delete!
  [_env props]
  (log/info :delete!/starting {:props props})
  (let [id (model-key props)]
    (a.n.pubkeys/delete! id)
    {::mu/status                   :ok
     ::r.n.pubkeys/deleted-records (m.n.pubkeys/idents [id])}))
