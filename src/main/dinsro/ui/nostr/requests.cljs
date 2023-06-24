(ns dinsro.ui.nostr.requests
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.requests.connections :as u.n.rq.connections]
   [dinsro.ui.nostr.requests.filter-items :as u.n.rq.filter-items]
   [dinsro.ui.nostr.requests.filters :as u.n.rq.filters]
   [dinsro.ui.nostr.requests.runs :as u.n.rq.runs]
   [lambdaisland.glogc :as log]))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.rq.connections/SubPage
    u.n.rq.filter-items/SubPage
    u.n.rq.filters/SubPage
    u.n.rq.runs/SubPage]})

(def ui-router (comp/factory Router))

(defsc Show
  [_this {::m.n.requests/keys [code relay]
          ::j.n.requests/keys [query-string]
          :ui/keys            [nav-menu router]}]
  {:ident         ::m.n.requests/id
   :initial-state (fn [props]
                    (log/trace :Show/initial-state {:props props})
                    (let [id (::m.n.requests/id props)]
                      {::m.n.requests/id           nil
                       :ui/nav-menu
                       (comp/get-initial-state
                        u.menus/NavMenu
                        {::m.navbars/id :nostr-requests :id id})
                       :ui/router                  (comp/get-initial-state Router)
                       ::m.n.requests/code         ""
                       ::m.n.requests/relay        (comp/get-initial-state u.links/RelayLinkForm)
                       ::j.n.requests/query-string ""}))
   :pre-merge     (u.loader/page-merger
                   ::m.n.requests/id
                   {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :nostr-requests}]
                    :ui/router   [Router {}]})
   :query         [::m.n.requests/id
                   ::m.n.requests/code
                   ::j.n.requests/query-string
                   {::m.n.requests/relay (comp/get-query u.links/RelayLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]
   :route-segment ["request" :id]
   :will-enter    (partial u.loader/page-loader ::m.n.requests/id ::Show)}
  (let [{:keys [main _sub]} (css/get-classnames Show)]
    (dom/div {:classes [main]}
      (dom/div :.ui.segment
        (dom/div "Request")
        (dom/div {} (str code))
        (dom/div {} (str "Query String: " query-string))
        (dom/div {} (u.links/ui-relay-link relay)))
      (u.menus/ui-nav-menu nav-menu)
      (ui-router router))))
