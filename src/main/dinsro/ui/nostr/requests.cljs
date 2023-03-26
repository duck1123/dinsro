(ns dinsro.ui.nostr.requests
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
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
  [_this {::m.n.requests/keys [code id start-time end-time relay]
          :ui/keys            [router]}]
  {:ident         ::m.n.requests/id
   :initial-state {::m.n.requests/id         nil
                   :ui/router                {}
                   ::m.n.requests/code       ""
                   ::m.n.requests/start-time nil
                   ::m.n.requests/end-time   nil
                   ::m.n.requests/relay      {}}
   :pre-merge     (u.links/page-merger ::m.n.requests/id {:ui/router Router})
   :query         [::m.n.requests/id
                   ::m.n.requests/code
                   ::m.n.requests/start-time
                   ::m.n.requests/end-time
                   {::m.n.requests/relay (comp/get-query u.links/RelayLinkForm)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["request" :id]
   :will-enter    (partial u.links/page-loader ::m.n.requests/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/div "Request")
        (dom/div {} (str code))
        (dom/div {} (str start-time))
        (dom/div {} (str end-time))
        (dom/div {} (u.links/ui-relay-link relay)))
      (u.links/ui-nav-menu {:menu-items menu-items :id id})
      ((comp/factory Router) router))))
