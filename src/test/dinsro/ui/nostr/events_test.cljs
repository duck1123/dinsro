(ns dinsro.ui.nostr.events-test
  (:require
   [dinsro.model.nostr.events :as m.n.events]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs :as ds]
   [dinsro.ui.nostr.events :as u.n.events]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard EventBox
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.n.events/EventBox
    ::ct.fulcro3/initial-state
    (fn []
      {::m.n.events/id         (ds/gen-key ::m.n.events/id)
       ::m.n.events/content    "Foo"
       ::m.n.events/created-at 0
       ::m.n.events/pubkey     {::m.n.pubkeys/id      (ds/gen-key ::m.n.pubkeys/id)
                                ::m.n.pubkeys/name    "Author Name"
                                ::m.n.pubkeys/hex     "deadbeef"
                                ::m.n.pubkeys/picture "https://duck1123.com/images/duck1123.png"}})}))
