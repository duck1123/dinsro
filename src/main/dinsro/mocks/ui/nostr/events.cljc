(ns dinsro.mocks.ui.nostr.events
  (:require
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.mocks.ui.nostr.event-tags :as mo.u.n.event-tags]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.options.nostr.connections :as o.n.connections]
   [dinsro.options.nostr.events :as o.n.events]
   [dinsro.options.nostr.pubkeys :as o.n.pubkeys]
   [dinsro.options.nostr.relays :as o.n.relays]
   [dinsro.options.nostr.witnesses :as o.n.witnesses]
   [dinsro.specs :as ds]))

;; [[../../../ui/nostr/events.cljc]]

(defn make-relay
  []
  {o.n.relays/id      (ds/gen-key o.n.relays/id)
   o.n.relays/address (ds/gen-key o.n.relays/address)})

(defn make-connection
  []
  {o.n.connections/id    (ds/gen-key o.n.connections/id)
   o.n.connections/relay (make-relay)})

(defn make-run
  []
  {::m.n.runs/id         nil
   ::m.n.runs/connection (make-connection)})

(defn make-witness
  []
  {o.n.witnesses/id (ds/gen-key o.n.witnesses/id)})

(defn make-pubkey
  []
  {o.n.pubkeys/id      (ds/gen-key o.n.pubkeys/id)
   o.n.pubkeys/name    (ds/gen-key o.n.pubkeys/name)
   o.n.pubkeys/hex     (ds/gen-key o.n.pubkeys/hex)
   o.n.pubkeys/picture "https://duck1123.com/images/duck1123.png"})

(defn EventAuthorImage-data
  [_a]
  (make-pubkey))

(defn EventAuthor-data
  [_a]
  (make-pubkey))

(defn EventBox-data
  []
  {o.n.events/id           (ds/gen-key o.n.events/id)
   o.n.events/pubkey       (make-pubkey)
   o.n.events/content      (ds/gen-key o.n.events/content)
   o.n.events/created-at   0
   ::j.n.events/created-date (ds/gen-key ::j.n.events/created-date)
   ::j.n.events/witnesses    (map (fn [_] (make-witness)) (range 3))
   ::j.n.events/tags         (map (fn [_] (mo.u.n.event-tags/make-tag)) (range 3))})

(def row-count 0)

(defn SubPage-row
  [_o]
  {o.n.events/id      (ds/gen-key o.n.events/id)
   o.n.events/content (ds/gen-key o.n.events/id)})

(defn EventReport-data
  []
  {:ui/controls       []
   :ui/current-rows   (map
                       (fn [_] (SubPage-row {}))
                       (range row-count))
   :ui/busy?          false
   :ui/parameters     {}
   :ui/page-count     1
   :ui/current-page   1
   :ui/cache          {}
   o.n.events/id      (ds/gen-key o.n.events/id)
   o.n.events/content (ds/gen-key o.n.events/id)})

(defn ShowEvent-data
  [_]
  (EventBox-data))
