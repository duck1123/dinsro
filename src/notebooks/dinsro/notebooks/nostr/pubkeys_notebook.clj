^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.notebooks.nostr.pubkeys_notebook
  (:require
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.relay-client :as a.n.relay-client]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.client.converters.byte-vector :as cs.byte-vector]
   [dinsro.client.scala :as cs]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.notebook-utils :as nu]
   [dinsro.processors.nostr.pubkeys :as p.n.pubkeys]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk])
  (:import org.bitcoins.core.util.Bech32$
           org.bitcoins.core.util.Bech32Encoding$Bech32$))

;; # Pubkeys

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

^{::clerk/visibility {:code :hide :result :hide}}
(comment)

^{::clerk/viewer clerk/html ::clerk/no-cache true}
(nu/x3)

;; The id we're working with

^{::clerk/viewer clerk/code ::clerk/visibility {:code :hide}}
(def pubkey-id (first (q.n.pubkeys/index-ids)))

;; Data stored in database

^{::clerk/viewer clerk/code ::clerk/visibility {:code :hide}}
(q.n.pubkeys/read-record pubkey-id)

;; ## Parsing content

;; This is a response from fetching a kind 0

^{::clerk/viewer clerk/code ::clerk/visibility {:code :hide}}
(def duck-content "{\"name\":\"dinsro\",\"about\":\"sats-first budget management\\n\\nhttps://github.com/duck1123/dinsro\",\"nip05\":\"dinsro@detbtc.com\",\"lud06\":\"lnurl1dp68gurn8ghj7cm0d9hx7uewd9hj7tnhv4kxctttdehhwm30d3h82unvwqhkgatrdvrwrevc\",\"lud16\":\"duck@coinos.io\",\"picture\":\"https://void.cat/d/JpoHXq8TQNpB7H6oCpTz6J\",\"website\":\"https://dinsro.com/\"}")

;; When parsed

^{::clerk/viewer clerk/code ::clerk/visibility {:code :hide}}
(a.n.pubkeys/parse-content duck-content)

;; ## Updating pubkey

;; This will run the update action

^{::clerk/visibility {:code :hide :result :hide}}
(def last-update-result (atom nil))

^{::clerk/visibility {:result :hide}}
(defn update-pubkey-run []
  (let [relay-id (first (q.n.relays/index-ids))
        result (nu/try-await (p.n.pubkeys/update-pubkey! pubkey-id relay-id))]
    (reset! last-update-result result)
    result))

;; last update result

^{::clerk/visibility {:code :hide}}
@last-update-result

^{::clerk/visibility {:result :hide}}
(comment (update-pubkey-run))

(comment

  (def relay-id (q.n.relays/register-relay "wss://relay.kronkltd.net"))
  (q.n.relays/read-record relay-id)

  a.n.relay-client/connections

  (q.n.pubkeys/index-ids)

  (a.n.relays/disconnect! relay-id)

  (def pubkey-id (first (q.n.pubkeys/index-ids)))
  pubkey-id

  (def hex (::m.n.pubkeys/hex (q.n.pubkeys/read-record pubkey-id)))
  hex

  ;; https://github.com/nostr-protocol/nips/blob/master/19.md
  (def hex2 "7e7e9c42a91bfef19fa929e5fda1b72e0ebc1a4c1141673e2794234d86addf4e")
  (def expected-npub "npub10elfcs4fr0l0r8af98jlmgdh9c8tcxjvz9qkw038js35mp4dma8qzvjptg")
  expected-npub

  (def bv (cs/get-or-nil (cs.byte-vector/->obj hex2)))
  bv
  (def data-5bit (.from8bitTo5bit Bech32$/MODULE$ bv))
  (def hrp-5bit (.hrpExpand Bech32$/MODULE$ "npub"))

  org.bitcoins.core.util.Bech32Encoding/Bech32m

  (.concat hrp-5bit data-5bit)
  (def checksum (.createChecksum Bech32$/MODULE$ (.concat hrp-5bit data-5bit)
                                 Bech32Encoding$Bech32$/MODULE$))

  (.encode5bitToString Bech32$/MODULE$ checksum)

  (a.n.pubkeys/bech32-encode hex2 "npub")

  (a.n.pubkeys/calculate-npub hex2)

  (a.n.pubkeys/fetch-pubkey! "3d842afecd5e293f28b6627933704a3fb8ce153aa91d790ab11f6a752d44a42d")

  nil)
