^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.notebooks.nostr.pubkeys_notebook
  (:require
   [dinsro.actions.nostr.pubkeys :as a.n.pubkeys]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; # Pubkeys


^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(comment
  ^{::clerk/viewer dv/file-link-viewer ::clerk/visibility :hide}
  (nu/display-file-links))

;; The id we're working with

^{::clerk/viewer clerk/code ::clerk/visibility :hide}
(def pubkey-id (first (q.n.pubkeys/index-ids)))

;; Data stored in database

^{::clerk/viewer clerk/code ::clerk/visibility :hide}
(q.n.pubkeys/read-record pubkey-id)

;; ## Parsing content

;; This is a response from fetching a kind 0

^{::clerk/viewer clerk/code ::clerk/visibility :hide}
(def duck-content "{\"name\":\"dinsro\",\"about\":\"sats-first budget management\\n\\nhttps://github.com/duck1123/dinsro\",\"nip05\":\"dinsro@detbtc.com\",\"lud06\":\"lnurl1dp68gurn8ghj7cm0d9hx7uewd9hj7tnhv4kxctttdehhwm30d3h82unvwqhkgatrdvrwrevc\",\"lud16\":\"duck@coinos.io\",\"picture\":\"https://void.cat/d/JpoHXq8TQNpB7H6oCpTz6J\",\"website\":\"https://dinsro.com/\"}")

;; When parsed

^{::clerk/viewer clerk/code ::clerk/visibility :hide}
(a.n.pubkeys/parse-content duck-content)

;; ## Updating pubkey

;; This will run the update action

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(def last-update-result (atom nil))

^{::clerk/viewer clerk/hide-result}
(defn update-pubkey-run []
  (reset! last-update-result (nu/try-await (a.n.pubkeys/update-pubkey! pubkey-id))))

;; last update result

^{::clerk/visibility :hide}
@last-update-result

^{::clerk/visibility :hide ::clerk/viewer clerk/hide-result}
(comment (update-pubkey-run))
