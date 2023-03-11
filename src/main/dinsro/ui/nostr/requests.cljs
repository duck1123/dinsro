(ns dinsro.ui.nostr.requests
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.react.error-boundaries :as eb]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.model.nostr.relays :as m.n.relays]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.requests.filters :as u.n.rq.filters]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.rq.filters/SubPage]})

(def menu-items
  [{:key   "filters"
    :name  "Filters"
    :route "dinsro.ui.nostr.requests.filters/SubPage"}])

(defsc Show
  [_this {::m.n.requests/keys [code id]
          :ui/keys            [router]
          :as                 props}]
  {:ident         ::m.n.requests/id
   :initial-state {::m.n.requests/id nil
                   :ui/router        {}}
   :pre-merge     (u.links/page-merger ::m.n.requests/id {:ui/router Router})
   :query         [::m.n.requests/id
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["request" :id]
   :will-enter    (partial u.links/page-loader ::m.n.relays/id ::Show)}
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (dom/div :.ui.segment
          (dom/div "Request")
          (dom/div {} (str code))
          (dom/dl {}))
        (u.links/ui-nav-menu {:menu-items menu-items :id id})
        (eb/error-boundary
         (if router
           ((comp/factory Router) router)
           (dom/div :.ui.segment
             (dom/h3 {} "Router not loaded")
             (u.links/ui-props-logger props))))))
    (dom/div :.ui.segment
      (dom/h3 {} "Item not loaded")
      (u.links/ui-props-logger props))))
