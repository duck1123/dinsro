(ns dinsro.ui.nostr.runs
  (:require
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.connections :as u.n.connections]))

;; [[../../actions/nostr/runs.clj]]
;; [[../../ui/admin/nostr/relays/runs.cljc]]
;; [[../../ui/admin/nostr/runs.cljc]]

(def model-key ::m.n.runs/id)

(defsc RunDisplay
  [_this {::m.n.runs/keys [connection] :as props}]
  {:ident         ::m.n.runs/id
   :initial-state {::m.n.runs/id         nil
                   ::m.n.runs/status     {}
                   ::m.n.runs/connection {}}
   :query         [::m.n.runs/id
                   ::m.n.runs/status
                   {::m.n.runs/connection (comp/get-query u.n.connections/ConnectionDisplay)}]}
  (dom/div {} (u.links/ui-admin-run-link props))
  (u.n.connections/ui-connection-display connection))

(def ui-run-display (comp/factory RunDisplay {:keyfn model-key}))
