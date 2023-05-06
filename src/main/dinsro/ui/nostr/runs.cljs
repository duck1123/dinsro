(ns dinsro.ui.nostr.runs
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.menus :as me]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.connections :as u.n.connections]
   [dinsro.ui.nostr.runs.witnesses :as u.n.r.witnesses]))

(defsc RunDisplay
  [_this {::m.n.runs/keys [connection] :as props}]
  {:ident         ::m.n.runs/id
   :initial-state {::m.n.runs/id         nil
                   ::m.n.runs/status     {}
                   ::m.n.runs/connection {}}
   :query         [::m.n.runs/id
                   ::m.n.runs/status
                   {::m.n.runs/connection (comp/get-query u.n.connections/ConnectionDisplay)}]}
  (dom/div {} (u.links/ui-run-link props))
  (u.n.connections/ui-connection-display connection))

(def ui-run-display (comp/factory RunDisplay {:keyfn ::m.n.runs/id}))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.r.witnesses/SubPage]})

(defsc Show
  [_this {::m.n.runs/keys [id]
          :ui/keys        [router]}]
  {:ident         ::m.n.runs/id
   :initial-state {::m.n.runs/id nil
                   :ui/router    {}}
   :pre-merge     (u.links/page-merger ::m.n.runs/id {:ui/router Router})
   :query         [::m.n.runs/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["run" :id]
   :will-enter    (partial u.links/page-loader ::m.n.runs/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/div "Run")
        (dom/div {} (str id)))
      (u.links/ui-nav-menu {:menu-items me/nostr-runs-menu-items :id id})
      ((comp/factory Router) router))))
