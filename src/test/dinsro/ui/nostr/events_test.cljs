(ns dinsro.ui.nostr.events-test
  (:require
   [dinsro.joins.nostr.events :as j.n.events]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.model.nostr.witnesses :as m.n.witnesses]
   [dinsro.specs :as ds]
   [dinsro.ui.nostr.event-tags-test :as t.u.n.event-tags]
   [dinsro.ui.nostr.events :as u.n.events]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

(defn make-relay
  []
  {::m.n.relays/id      (ds/gen-key ::m.n.relays/id)
   ::m.n.relays/address (ds/gen-key ::m.n.relays/address)})

(defn make-connection
  []
  {::m.n.connections/id    (ds/gen-key ::m.n.connections/id)
   ::m.n.connections/relay (make-relay)})

(defn make-run
  []
  {::m.n.runs/id         nil
   ::m.n.runs/connection (make-connection)})

(defn make-witness
  []
  {::m.n.witnesses/id  (ds/gen-key ::m.n.witnesses/id)
   ::m.n.witnesses/run (make-run)})

(defn make-pubkey
  []
  {::m.n.pubkeys/id      (ds/gen-key ::m.n.pubkeys/id)
   ::m.n.pubkeys/name    "Author Name"
   ::m.n.pubkeys/hex     "deadbeef"
   ::m.n.pubkeys/picture "https://duck1123.com/images/duck1123.png"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard EventAuthor
  {}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root          u.n.events/EventAuthor
    ::ct.fulcro3/initial-state (fn [] (make-pubkey))}))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard EventBox
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.n.events/EventBox
    ::ct.fulcro3/initial-state
    (fn []
      {::m.n.events/id           (ds/gen-key ::m.n.events/id)
       ::m.n.events/pubkey       (make-pubkey)
       ::m.n.events/content      "Foo"
       ::m.n.events/created-at   0
       ::j.n.events/created-date (ds/gen-key ::j.n.events/created-date)
       ::j.n.events/witnesses    (map (fn [_] (make-witness)) (range 3))
       ::j.n.events/tags         (map (fn [_] (t.u.n.event-tags/make-tag)) (range 3))})}))
