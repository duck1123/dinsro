^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.notebooks.nostr.pubkeys_notebook
  (:require
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.actions.nostr.relays :as a.n.relays]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

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
        result (nu/try-await (a.n.pubkeys/update-pubkey! pubkey-id relay-id))]
    (reset! last-update-result result)
    result))

;; last update result

^{::clerk/visibility {:code :hide}}
@last-update-result

^{::clerk/visibility {:result :hide}}
(comment (update-pubkey-run))

(comment

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id
  (def relay (q.n.relays/read-record relay-id))

  (a.n.relays/connect! relay-id)
  (a.n.relays/disconnect! relay-id)

  relay

  (a.n.relays/get-client-for-id relay-id false)

  nil)
