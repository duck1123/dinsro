(ns dinsro.ui.nostr.requests
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.menus :as me]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.nostr.requests.connections :as u.n.rq.connections]
   [dinsro.ui.nostr.requests.filter-items :as u.n.rq.filter-items]
   [dinsro.ui.nostr.requests.filters :as u.n.rq.filters]
   [dinsro.ui.nostr.requests.runs :as u.n.rq.runs]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.rq.connections/SubPage
    u.n.rq.filter-items/SubPage
    u.n.rq.filters/SubPage
    u.n.rq.runs/SubPage]})

(defsc Show
  [_this {::m.n.requests/keys [code id start-time end-time relay]
          ::j.n.requests/keys [query-string]
          :ui/keys            [router]}]
  {:ident         ::m.n.requests/id
   :initial-state {::m.n.requests/id         nil
                   :ui/router                {}
                   ::m.n.requests/code       ""
                   ::m.n.requests/start-time nil
                   ::m.n.requests/end-time   nil
                   ::m.n.requests/relay      {}
                   ::j.n.requests/query-string ""}
   :pre-merge     (u.links/page-merger ::m.n.requests/id {:ui/router Router})
   :query         [::m.n.requests/id
                   ::m.n.requests/code
                   ::m.n.requests/start-time
                   ::m.n.requests/end-time
                   ::j.n.requests/query-string
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
        (dom/div {} (str "Query String: " query-string))
        (dom/div {} (u.links/ui-relay-link relay)))
      (u.links/ui-nav-menu {:menu-items me/nostr-requests-menu-items :id id})
      ((comp/factory Router) router))))
