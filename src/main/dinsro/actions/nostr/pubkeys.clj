(ns dinsro.actions.nostr.pubkeys
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>defn ? =>]]
   [dinsro.actions.contacts :as a.contacts]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [dinsro.specs :as ds]
   [hato.websocket :as ws]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/pubkeys.cljc][Pubkey Joins]]
;; [[../../model/nostr/pubkeys.cljc][Pubkey Model]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkey Mutations]]
;; [[../../queries/nostr/pubkeys.clj][Pubkey Queries]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkey UI]]

(>defn send-adhoc-request
  [client hex]
  [any? ::m.n.pubkeys/hex => any?]
  (let [msg (json/json-str (a.n.relay-client/adhoc-request [hex]))]
    (ws/send! client msg)))

(>defn poll!
  [relay-id]
  [::m.n.relays/id => any?]
  (let [relay   (q.n.relays/read-record relay-id)
        address (::m.n.relays/address relay)
        chan    (a.n.relay-client/get-channel address)]
    (async/<!! (a.n.relays/take-timeout (a.n.relays/process-messages chan)))))

(>defn parse-content-parsed
  [data]
  [map? => (s/keys)]
  (log/info :parse-content-parsed/starting {:data data})
  (let [{name    "name"
         about   "about"
         nip05   "nip05"
         lud06   "lud06"
         lud16   "lud16"
         picture "picture"
         website "website"} data
        content             {::m.n.pubkeys/name    name
                             ::m.n.pubkeys/about   about
                             ::m.n.pubkeys/nip05   nip05
                             ::m.n.pubkeys/lud06   lud06
                             ::m.n.pubkeys/lud16   lud16
                             ::m.n.pubkeys/picture picture
                             ::m.n.pubkeys/website website}]
    (log/info :parse-content-parsed/finished {:content content})
    content))

(>defn parse-content
  [content]
  [string? => any?]
  (log/info :parse-content/starting {:content content})
  (let [data (json/read-str content)]
    (log/info :parse-content/read {:data data})
    (let [parsed (parse-content-parsed data)]
      (log/info :parse-content/parsed {:parsed parsed})
      parsed)))

(>defn register-subscription!
  [relay-id pubkey-id]
  [::m.n.relays/id ::m.n.pubkeys/id => ::m.n.subscriptions/id]
  (log/info :register-subscription!/starting {:relay-id relay-id :pubkey-id pubkey-id})
  (if-let [subscription-id (q.n.subscriptions/find-by-relay-and-pubkey relay-id pubkey-id)]
    (do
      (log/info :register-subscription!/found {:subscription-id subscription-id})
      subscription-id)
    (do
      (log/info :register-subscription!/not-found {})
      (let [code            "adhoc"
            params          {::m.n.subscriptions/code  code
                             ::m.n.subscriptions/relay relay-id}
            subscription-id (q.n.subscriptions/create-record params)]
        (log/info :register-subscription!/created {:subscription-id subscription-id})
        subscription-id))))

(>defn register-pubkey!
  [pubkey-hex]
  [::m.n.pubkeys/hex => ::m.n.pubkeys/id]
  (log/info :register-pubkey!/starting {:pubkey-hex pubkey-hex})
  (let [pubkey-id (if-let [pubkey-id (q.n.pubkeys/find-by-hex pubkey-hex)]
                    pubkey-id
                    (q.n.pubkeys/create-record {::m.n.pubkeys/hex pubkey-hex}))]
    (log/info :register-pubkey!/registered {:pubkey-id pubkey-id})
    pubkey-id))

(>defn process-pubkey-tags!
  [tags]
  [(s/coll-of any?) => any?]
  (log/info :process-pubkey-tags!/starting {:tags tags})
  (doseq [tag tags]
    (log/info :process-pubkey-tags!/tag {:tag tag})
    (let [[_ pubkey-hex relay-address] tag]
      (log/info :process-pubkey-tags!/parsed {:pubkey-hex pubkey-hex :relay-address relay-address})
      (register-pubkey! pubkey-hex)
      (a.n.relays/register-relay! relay-address))))

(>defn process-pubkey-data!
  [pubkey-hex content tags]
  [string? string? (s/coll-of any?) => any?]
  (log/info :process-pubkey-data!/starting {:content content :tags tags})
  (process-pubkey-tags! tags)
  (if-let [pubkey-id (register-pubkey! pubkey-hex)]
    (let [parsed (parse-content-parsed (json/read-str content))]
      (log/info :process-pubkey-data!/parsed {:parsed parsed})
      (q.n.pubkeys/update! pubkey-id parsed))
    (throw (RuntimeException. "failed to find pubkey"))))

(>defn process-pubkey-message!
  [event-type code body]
  [string? string? map? => nil?]
  (log/info :process-pubkey-message!/starting {:event-type event-type :code code :body body})
  (let [content    (get body "content")
        id         (get body "id")
        sig        (get body "sig")
        created-at (get body "created_at")
        kind       (get body "kind")
        pubkey-hex (get body "pubkey")
        tags       (get body "tags")]
    (log/info :process-pubkey-message!/content {:id         id
                                                :sig        sig
                                                :created-at created-at
                                                :kind       kind
                                                :pubkey     pubkey-hex
                                                :tags       tags
                                                :content    content})
    (process-pubkey-data! pubkey-hex content tags)))

(>defn fetch-pubkey!
  "Fetch info about pubkey from relay"
  ([pubkey]
   [::m.n.pubkeys/hex => ds/channel?]
   (if-let [relay-id (first (q.n.relays/index-ids))]
     (fetch-pubkey! pubkey relay-id)
     (throw (RuntimeException. "No relays"))))
  ([pubkey-hex relay-id]
   [::m.n.pubkeys/hex ::m.n.relays/id => ds/channel?]
   (async/go
     (log/info :fetch-pubkey!/starting {:pubkey-hex pubkey-hex :relay-id relay-id})
     (let [body    {:authors [pubkey-hex] :kinds [0]}
           chan    (a.n.relays/send! relay-id body)
           message (async/<! (a.n.relays/process-messages chan))]
       (log/info :fetch-pubkey!/fetched {:message message})
       (let [{:keys [req-id tags id pow notified sig content]} message]
         (if content
           (let [body                                              (json/read-str content)]
             (log/info :fetch-pubkey!/parsed {:req-id   req-id
                                              :tags     tags
                                              :id       id
                                              :pow      pow
                                              :notified notified
                                              :sig      sig
                                              :body     body})
             (process-pubkey-data! pubkey-hex body tags)
             message)
           (throw (RuntimeException. "No content"))))))))

(>defn update-pubkey!
  [pubkey-id]
  [::m.n.pubkeys/id => any?]
  (log/info :update-pubkey!/starting {:pubkey-id pubkey-id})
  (async/go
    (if-let [pubkey (q.n.pubkeys/read-record pubkey-id)]
      (let [hex      (::m.n.pubkeys/hex pubkey)
            response (async/<! (fetch-pubkey! hex))]
        (log/info :update-pubkey!/finished {:response response})
        response)
      (throw (RuntimeException. "No pubkey")))))

(>defn fetch-contact!
  [pubkey-id]
  [::m.n.pubkeys/id => ds/channel?]
  (update-pubkey! pubkey-id))

(defn start-pubkey-listener!
  [channel]
  (log/info :start-pubkey-listener!/starting {})
  (async/go-loop []
    (if-let [msg (async/<! channel)]
      (do
        (let [[event-type code body] msg]
          (process-pubkey-message! event-type code body))
        (recur))
      (do
        (log/info :start-pubkey-listener!/no-message {})
        nil))))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))
  (q.n.relays/read-record relay-id)

  (def duck-content "{\"name\":\"dinsro\",\"about\":\"sats-first budget management\\n\\nhttps://github.com/duck1123/dinsro\",\"nip05\":\"dinsro@detbtc.com\",\"lud06\":\"lnurl1dp68gurn8ghj7cm0d9hx7uewd9hj7tnhv4kxctttdehhwm30d3h82unvwqhkgatrdvrwrevc\",\"lud16\":\"duck@coinos.io\",\"picture\":\"https://void.cat/d/JpoHXq8TQNpB7H6oCpTz6J\",\"website\":\"https://dinsro.com/\"}")

  a.n.relay-client/connections

  (a.n.relays/disconnect! relay-id)

  (def pubkey-id (first (q.n.pubkeys/index-ids)))
  pubkey-id

  (q.n.pubkeys/read-record pubkey-id)

  (def contact (fetch-contact! pubkey-id))

  contact

  (poll! relay-id)

  (def response (a.n.relays/get-client-for-id relay-id))

  response
  (def chan (:chan response))
  (def client (:client response))
  client

  (send-adhoc-request client a.contacts/duck)
  (send-adhoc-request client a.contacts/matt-odell)

  (ws/send! client (json/json-str (a.n.relay-client/adhoc-request [a.contacts/duck])))

  (async/poll! (a.n.relays/process-messages chan))

  (fetch-pubkey! a.contacts/duck)

  (async/<!! (a.n.relays/take-timeout (a.n.relays/process-messages chan)))

  chan

  a.n.relay-client/connections

  nil)
