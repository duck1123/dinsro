(ns dinsro.ui.admin.nostr.requests
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.menus :as me]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.ui.admin.nostr.requests.connections :as u.a.n.rq.connections]
   [dinsro.ui.admin.nostr.requests.filter-items :as u.a.n.rq.filter-items]
   [dinsro.ui.admin.nostr.requests.filters :as u.a.n.rq.filters]
   [dinsro.ui.admin.nostr.requests.runs :as u.a.n.rq.runs]
   [dinsro.ui.links :as u.links]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.rq.connections/SubPage
    u.a.n.rq.filter-items/SubPage
    u.a.n.rq.filters/SubPage
    u.a.n.rq.runs/SubPage]})

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
      (u.links/ui-nav-menu {:menu-items me/admin-nostr-requests-menu-items :id id})
      ((comp/factory Router) router))))

(report/defsc-report Report
  [_this _props]
  {ro/columns           [m.n.requests/id
                         m.n.requests/code]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/route             "requests"
   ro/row-pk            m.n.requests/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.requests/index
   ro/title             "Requests"})
