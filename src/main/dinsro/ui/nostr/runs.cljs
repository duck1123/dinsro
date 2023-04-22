(ns dinsro.ui.nostr.runs
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.nostr.runs :as m.n.runs]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.runs.witnesses :as u.n.r.witnesses]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.r.witnesses/SubPage]})

(def menu-items
  [{:key   "witnesses"
    :name  "Witnesses"
    :route "dinsro.ui.nostr.runs.witnesses/SubPage"}])

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
      (u.links/ui-nav-menu {:menu-items menu-items :id id})
      ((comp/factory Router) router))))
