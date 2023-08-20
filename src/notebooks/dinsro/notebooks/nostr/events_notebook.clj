^{:nextjournal.clerk/visibility #{:hide-ns}}
(ns dinsro.notebooks.nostr.events-notebook
  (:require
   [com.fulcrologic.rad.ids :refer [new-uuid]]
   [dinsro.actions.nostr.events :as a.n.events]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.notebook-utils :as nu]
   [dinsro.queries.nostr.events :as q.n.events]
   [dinsro.queries.nostr.pubkeys :as q.n.pubkeys]
   [dinsro.queries.nostr.relays :as q.n.relays]
   [dinsro.specs :as ds]
   [dinsro.viewers :as dv]
   [nextjournal.clerk :as clerk]))

;; [[../../../../main/dinsro/actions/nostr/events.clj]]

;; # Nostr Events

^{::clerk/viewer dv/file-link-viewer ::clerk/visibility {:code :hide}}
(nu/display-file-links)

(ds/gen-key ::m.n.events/item)

(comment

  (q.n.pubkeys/index-ids)

  (def alice-id (first (q.n.pubkeys/find-by-name "alice")))
  (def duck-id (first (q.n.pubkeys/find-by-name "duck")))

  (a.n.events/fetch-by-note-id "e4f5b8f980885e5f013d1b0549ce871c42d892e744da3e4a611a65202a227472")
  (a.n.events/fetch-by-note-id "36df49af7fe181520beee31644f121ea2bb8e4ff99468d08f56040e5b792bea5")

  (def event (q.n.events/read-record (new-uuid "0186ae78-ae3d-8ab5-8af2-907aa8716e04")))
  event

  (def relay-id (first (q.n.relays/index-ids)))
  relay-id

  (a.n.events/fetch-by-note-id (::m.n.events/note-id event) relay-id)

  (q.n.pubkeys/read-record alice-id)
  (q.n.pubkeys/read-record duck-id)

  (q.n.events/count-ids)

  (q.n.events/find-by-author duck-id)
  (q.n.events/find-by-author alice-id)

  (map q.n.events/read-record (q.n.events/index-ids))

  (def message "👀 https://nostr.build/i/nostr.build_9e6becea72a9673f6e33ade5fa7961728fb3758df5d56e376acb89f10e1c242e.jpeg https://nostr.build/i/nostr.build_fb5377f37c0dcedf5c88507b157b513c3ae75839fd43e5d6faa237c0b9f0d6e3.jpeg")

  (def dperini-matcher
    #"(?:(?:(?:https?|ftp):)?\/\/)(?:\S+(?::\S*)?@)?(?:(?!(?:10|127)(?:\.\d{1,3}){3})(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z0-9\u00a1-\uffff][a-z0-9\u00a1-\uffff_-]{0,62})?[a-z0-9\u00a1-\uffff]\.)+(?:[a-z\u00a1-\uffff]{2,}\.?))(?::\d{2,5})?(?:[/?#]\S*)?")

  (re-find dperini-matcher message)

  (re-find #"https?:\/\/[-a-zA-Z0-9@:%._\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)" message)

  (a.n.events/extract-urls message)

  nil)
