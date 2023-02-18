(ns dinsro.actions.nostr.pubkeys
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.subscriptions :as m.n.subscriptions]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.queries.nostr.subscriptions :as q.n.subscriptions]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log]))

;; [[../../joins/nostr/pubkeys.cljc][Pubkey Joins]]
;; [[../../model/nostr/pubkeys.cljc][Pubkey Model]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkey Mutations]]
;; [[../../queries/nostr/pubkeys.clj][Pubkey Queries]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkey UI]]

(>defn parse-content-parsed
  [data]
  [map? => (s/keys)]
  (log/finer :parse-content-parsed/starting {:data data})
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
    (log/finer :parse-content-parsed/finished {:content content})
    content))

(>defn parse-content
  [content]
  [string? => any?]
  (log/finer :parse-content/starting {:content content})
  (let [data (json/read-str content)]
    (log/finer :parse-content/read {:data data})
    (let [parsed (parse-content-parsed data)]
      (log/finer :parse-content/parsed {:parsed parsed})
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
  [string? map? (s/coll-of any?) => any?]
  (log/info :process-pubkey-data!/starting {:pubkey-hex pubkey-hex
                                            :content    content
                                            :tags       tags})
  (process-pubkey-tags! tags)
  (if-let [pubkey-id (register-pubkey! pubkey-hex)]
    (let [parsed (parse-content-parsed content)]
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
    (let [content-data (json/read-str content)]
      (process-pubkey-data! pubkey-hex content-data tags))))

(>def ::req-id string?)
(>def ::tags (s/coll-of any?))
(>def ::id string?)
(>def ::sig string?)
(>def ::content string?)
(>def ::message (s/keys :req-un [::req-id ::tags ::id
                                 ::sig ::content]))

(>defn process-fetch-pubkey-message
  [output-chan pubkey-hex message]
  [ds/channel? ::m.n.pubkeys/hex ::message => nil?]
  (log/info :process-fetch-pubkey-message/fetched {:pubkey-hex pubkey-hex :message message})
  (let [{:keys [req-id tags content]} message]
    (if content
      (let [body (json/read-str content)]
        (log/info :process-fetch-pubkey-message/parsed {:req-id req-id})
        (let [response (process-pubkey-data! pubkey-hex body tags)]
          (log/info :process-fetch-pubkey-message/processed {:response response})))
      (do
        (log/info :process-fetch-pubkey-message/no-content {})
        (async/close! output-chan)
        nil))))

(>defn fetch-pubkey!
  "Fetch info about pubkey from relay"
  ([pubkey]
   [::m.n.pubkeys/hex => ds/channel?]
   (doseq [relay-id (q.n.relays/index-ids)]
     (fetch-pubkey! pubkey relay-id)
     #_(throw (RuntimeException. "No relays"))))
  ([pubkey-hex relay-id]
   [::m.n.pubkeys/hex ::m.n.relays/id => ds/channel?]
   (let [output-chan (async/chan)]
     (log/info :fetch-pubkey!/starting {:pubkey-hex pubkey-hex :relay-id relay-id})
     (let [body {:authors [pubkey-hex] :kinds [0]}
           chan (a.n.relays/send! relay-id body)]
       (log/info :fetch-pubkey!/sent {:chan chan})
       (async/go-loop []
         (let [message (async/<! chan)]
           (log/info :fetch-pubkey!/processing {:message message})
           (process-fetch-pubkey-message output-chan pubkey-hex message)
           (recur)))
       output-chan))))

(>defn update-pubkey!
  [pubkey-id]
  [::m.n.pubkeys/id => any?]
  (log/finer :update-pubkey!/starting {:pubkey-id pubkey-id})
  (async/go
    (if-let [pubkey (q.n.pubkeys/read-record pubkey-id)]
      (let [hex      (::m.n.pubkeys/hex pubkey)
            response (async/<! (fetch-pubkey! hex))]
        (log/finer :update-pubkey!/finished {:response response})
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

  a.n.relay-client/connections

  (a.n.relays/disconnect! relay-id)

  (def pubkey-id (first (q.n.pubkeys/index-ids)))
  pubkey-id

  (q.n.pubkeys/read-record pubkey-id)

  (def contact (fetch-contact! pubkey-id))
  contact

  (def response (a.n.relays/get-client-for-id relay-id))
  response

  (def chan (:chan response))
  chan

  (def client (:client response))
  client

  nil)
