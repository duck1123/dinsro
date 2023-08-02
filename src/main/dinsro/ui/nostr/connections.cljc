(ns dinsro.ui.nostr.connections
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [dinsro.model.nostr.connections :as m.n.connections]
   [dinsro.ui.links :as u.links]))

;; [[../../ui/admin/nostr/connections.cljs]]
;; [[../../ui/nostr/connections/runs.cljs]]

(defsc ConnectionDisplay
  [_this {::m.n.connections/keys [relay] :as props}]
  {:ident         ::m.n.connections/id
   :initial-state {::m.n.connections/id     nil
                   ::m.n.connections/status :initial
                   ::m.n.connections/relay  {}}
   :query         [::m.n.connections/id
                   ::m.n.connections/status
                   {::m.n.connections/relay (comp/get-query u.links/RelayLinkForm)}]}
  (u.links/ui-admin-relay-link props)
  (u.links/ui-admin-relay-link relay))

(def ui-connection-display (comp/factory ConnectionDisplay {:keyfn ::m.n.connections/id}))
