(ns dinsro.ui.nostr.event-tags-test
  (:require
   [dinsro.model.nostr.event-tags :as m.n.event-tags]
   [dinsro.specs :as ds]
   [dinsro.ui.nostr.event-tags :as u.n.event-tags]
   [nubank.workspaces.card-types.fulcro3 :as ct.fulcro3]
   [nubank.workspaces.core :as ws]))

(defn make-tag
  []
  {::m.n.event-tags/id        (ds/gen-key ::m.n.event-tags/id)
   ::m.n.event-tags/pubkey    nil
   ::m.n.event-tags/event     nil
   ::m.n.event-tags/index     (ds/gen-key ::m.n.event-tags/index)
   ::m.n.event-tags/raw-value (ds/gen-key ::m.n.event-tags/raw-value)
   ::m.n.event-tags/type      "e"})

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(ws/defcard TagDisplay
  (ct.fulcro3/fulcro-card
   {::ct.fulcro3/root u.n.event-tags/TagDisplay
    ::ct.fulcro3/initial-state
    (fn [] (make-tag))}))
