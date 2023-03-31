(ns dinsro.ui.nostr.pubkeys-test
  (:require
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.specs :as ds]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]
   [nubank.workspaces.model :as wsm]))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard PubkeyInfo
  {::wsm/card-height 12
   ::wsm/card-width  4}
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.n.pubkeys/PubkeyInfo
    ::ct.fulcro3/initial-state
    (fn []
      {::m.n.pubkeys/display-name "Duck Nebuchadnezzar"
       ::m.n.pubkeys/id      (ds/gen-key ::m.n.pubkeys/id)
       ::m.n.pubkeys/hex     "deadbeef"
       ::m.n.pubkeys/picture "https://duck1123.com/images/duck1123.png"
       ::m.n.pubkeys/website "https://duck1123.com/"
       ::m.n.pubkeys/lud06 ""
       ::m.n.pubkeys/about "Duck's just this guy, you know?"})}))
