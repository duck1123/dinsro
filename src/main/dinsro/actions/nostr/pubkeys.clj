(ns dinsro.actions.nostr.pubkeys
  (:require
   [clojure.core.async :as async]
   [clojure.data.json :as json]
   [clojure.spec.alpha :as s]
   [com.fulcrologic.guardrails.core :refer [>def >defn ? =>]]
   [dinsro.actions.nostr.filter-items :as a.n.filter-items]
   [dinsro.actions.nostr.filters :as a.n.filters]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.actions.nostr.requests :as a.n.requests]
   [dinsro.client.converters.byte-vector :as cs.byte-vector]
   [dinsro.client.scala :as cs]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.specs :as ds]
   [lambdaisland.glogc :as log])
  (:import org.bitcoins.core.util.Bech32$
           org.bitcoins.core.util.Bech32Encoding$Bech32$))

;; [[../../joins/nostr/pubkeys.cljc][Pubkey Joins]]
;; [[../../model/nostr/pubkeys.cljc][Pubkey Model]]
;; [[../../mutations/nostr/pubkeys.cljc][Pubkey Mutations]]
;; [[../../queries/nostr/pubkeys.clj][Pubkey Queries]]
;; [[../../ui/nostr/pubkeys.cljs][Pubkey UI]]

(>defn parse-content-parsed
  [data]
  [map? => (s/keys)]
  (let [{:keys [name about nip05 lud06 lud16 picture display_name website]} data
        content             {::m.n.pubkeys/name    name
                             ::m.n.pubkeys/about   about
                             ::m.n.pubkeys/diplay-name display_name
                             ::m.n.pubkeys/nip05   nip05
                             ::m.n.pubkeys/lud06   lud06
                             ::m.n.pubkeys/lud16   lud16
                             ::m.n.pubkeys/picture picture
                             ::m.n.pubkeys/website website}]
    content))

(>defn parse-content
  [content]
  [string? => any?]
  (let [data   (json/read-str content)
        parsed (parse-content-parsed data)]
    parsed))

(>defn register-pubkey!
  [pubkey-hex]
  [::m.n.pubkeys/hex => ::m.n.pubkeys/id]
  (log/debug :register-pubkey!/starting {:pubkey-hex pubkey-hex})
  (let [pubkey-id (if-let [pubkey-id (q.n.pubkeys/find-by-hex pubkey-hex)]
                    pubkey-id
                    (q.n.pubkeys/create-record {::m.n.pubkeys/hex pubkey-hex}))]
    (log/trace :register-pubkey!/registered {:pubkey-id pubkey-id})
    pubkey-id))

(>defn process-pubkey-tags!
  [tags]
  [(s/coll-of any?) => any?]
  (doseq [tag tags]
    (let [[_ pubkey-hex relay-address] tag]
      (register-pubkey! pubkey-hex)
      (a.n.relays/register-relay! relay-address))))

(>defn process-pubkey-data!
  [pubkey-hex content tags]
  [string? map? (s/coll-of any?) => any?]
  (process-pubkey-tags! tags)
  (if-let [pubkey-id (register-pubkey! pubkey-hex)]
    (let [parsed (parse-content-parsed content)]
      (q.n.pubkeys/update! pubkey-id parsed))
    (throw (ex-info "failed to find pubkey" {}))))

(>defn process-pubkey-message!
  [body]
  [map? => nil?]
  (let [content      (get body "content")
        pubkey-hex   (get body "pubkey")
        tags         (get body "tags")
        content-data (json/read-str content :key-fn keyword)]
    (process-pubkey-data! pubkey-hex content-data tags)))

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

;;   "Fetch info about pubkey from relay"

(>defn fetch-pubkey!
  "Fetch the kind 0 information given a pubkey hex"
  ([pubkey-hex]
   [::m.n.pubkeys/hex => ds/channel?]
   (do
     (log/warn :fetch-pubkey!/no-relay {:pubkey-hex pubkey-hex})
     (let [relay-ids (q.n.relays/index-ids)]
       (doseq [relay-id relay-ids]
         (fetch-pubkey! pubkey-hex relay-id)))))
  ([pubkey-hex relay-id]
   [::m.n.pubkeys/hex ::m.n.relays/id => ds/channel?]
   (let [output-chan (async/chan)]
     (log/info :fetch-pubkey!/starting {:pubkey-hex pubkey-hex :relay-id relay-id})
     (let [code (a.n.requests/get-next-code!)
           request-id (a.n.requests/register-request relay-id code)
           filter-id  (a.n.filters/register-filter! request-id)
           pubkey-id  (register-pubkey! pubkey-hex)
           item-id    (a.n.filter-items/register-pubkey! filter-id pubkey-id)]
       (log/info :fetch-pubkey!/item-created {:item-id item-id})
       (let [body {:authors [pubkey-hex] :kinds [0]}
             chan (a.n.relays/send! relay-id code body)]
         (async/go-loop []
           (if-let [message (async/<! chan)]
             (do
               (log/info :fetch-pubkey!/processing {:message message})
               (process-fetch-pubkey-message output-chan pubkey-hex message)
               (recur))
             (log/info :fetch-pubkey!/no-message {:pubkey-hex pubkey-hex})))
         output-chan)))))

(>defn create-pubkey-update-request
  [pubkey-id relay-id]
  [::m.n.pubkeys/id ::m.n.relays/id => ::m.n.requests/id]
  (let [request-id (a.n.requests/register-request relay-id)
        filter-id  (a.n.filters/register-filter! request-id)]
    (a.n.filter-items/register-pubkey! filter-id pubkey-id)
    (a.n.filter-items/register-kind! filter-id 0)
    request-id))

(defn start-pubkey-listener!
  [channel]
  (log/info :start-pubkey-listener!/starting {})
  (async/go-loop []
    (if-let [msg (async/<! channel)]
      (do
        (let [[_event-type _code body] msg]
          (process-pubkey-message! body))
        (recur))
      (do
        (log/info :start-pubkey-listener!/no-message {})
        nil))))

(defn add-contact!
  [props]
  (log/info :add-contact!/starting {:props props}))

(defn bech32-encode [hex-string prefix]
  (let [bv            (cs/get-or-nil (cs.byte-vector/->obj hex-string))
        hrp-5bit      (.hrpExpand Bech32$/MODULE$ "npub")
        data-5bit     (.from8bitTo5bit Bech32$/MODULE$ bv)
        data-part     (.encode8bitToString Bech32$/MODULE$ bv)
        checksum      (.createChecksum Bech32$/MODULE$ (.concat hrp-5bit data-5bit) Bech32Encoding$Bech32$/MODULE$)
        checksum-part (.encode5bitToString Bech32$/MODULE$ checksum)]
    (str prefix "1" data-part checksum-part)))

;; https://bitcoin-s.org/api/org/bitcoins/core/util/Bech32.html
(defn calculate-npub
  [hex]
  (bech32-encode hex "npub"))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))
  (q.n.relays/read-record relay-id)

  a.n.relay-client/connections

  (q.n.pubkeys/index-ids)

  (a.n.relays/disconnect! relay-id)

  (def pubkey-id (first (q.n.pubkeys/index-ids)))
  pubkey-id

  (def hex (::m.n.pubkeys/hex (q.n.pubkeys/read-record pubkey-id)))

  ;; https://github.com/nostr-protocol/nips/blob/master/19.md
  (def hex2 "7e7e9c42a91bfef19fa929e5fda1b72e0ebc1a4c1141673e2794234d86addf4e")
  (def expected-npub "npub10elfcs4fr0l0r8af98jlmgdh9c8tcxjvz9qkw038js35mp4dma8qzvjptg")

  (def bv (cs/get-or-nil (cs.byte-vector/->obj hex2)))
  bv
  (def data-5bit (.from8bitTo5bit Bech32$/MODULE$ bv))
  (def hrp-5bit (.hrpExpand Bech32$/MODULE$ "npub"))

  org.bitcoins.core.util.Bech32Encoding/Bech32m

  (.concat hrp-5bit data-5bit)
  (def checksum (.createChecksum Bech32$/MODULE$ (.concat hrp-5bit data-5bit)
                                 Bech32Encoding$Bech32$/MODULE$))

  (.encode5bitToString Bech32$/MODULE$ checksum)

  (bech32-encode hex2 "npub")

  (calculate-npub hex2)

  (fetch-pubkey! "3d842afecd5e293f28b6627933704a3fb8ce153aa91d790ab11f6a752d44a42d")

  nil)
