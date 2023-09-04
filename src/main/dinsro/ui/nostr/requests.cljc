(ns dinsro.ui.nostr.requests
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.semantic-ui.elements.segment.ui-segment :refer [ui-segment]]
   [dinsro.joins.nostr.requests :as j.n.requests]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.requests :as m.n.requests]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.requests.connections :as u.n.rq.connections]
   [dinsro.ui.nostr.requests.filter-items :as u.n.rq.filter-items]
   [dinsro.ui.nostr.requests.filters :as u.n.rq.filters]
   [dinsro.ui.nostr.requests.runs :as u.n.rq.runs]
   [lambdaisland.glogc :as log]))

;; [[../../model/nostr/requests.cljc]]

(def model-key ::m.n.requests/id)
(def parent-router-id :nostr)
(def required-role :user)
(def show-page-id :nostr-requests-show)

(defrouter Router
  [_this _props]
  {:router-targets
   [u.n.rq.connections/SubPage
    u.n.rq.filter-items/SubPage
    u.n.rq.filters/SubPage
    u.n.rq.runs/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent parent-router-id
   ::m.navbars/children
   [u.n.rq.filters/index-page-id
    u.n.rq.filter-items/index-page-id
    u.n.rq.runs/index-page-id
    u.n.rq.connections/index-page-id]})

(defsc Show
  [_this {::m.n.requests/keys [code id relay]
          ::j.n.requests/keys [query-string]
          :ui/keys            [nav-menu router]
          :as                 props}]
  {:ident         ::m.n.requests/id
   :initial-state (fn [props]
                    (log/trace :Show/initial-state {:props props})
                    (let [id (::m.n.requests/id props)]
                      {::m.n.requests/id           nil
                       :ui/nav-menu                (comp/get-initial-state u.menus/NavMenu {::m.navbars/id :nostr-requests :id id})
                       :ui/router                  (comp/get-initial-state Router)
                       ::m.n.requests/code         ""
                       ::m.n.requests/relay        (comp/get-initial-state u.links/RelayLinkForm)
                       ::j.n.requests/query-string ""}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id :nostr-requests}]
                     :ui/router   [Router {}]})
   :query         [::m.n.requests/id
                   ::m.n.requests/code
                   ::j.n.requests/query-string
                   {::m.n.requests/relay (comp/get-query u.links/RelayLinkForm)}
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (log/info :Show/starting {:props props})
  (if id
    (let [{:keys [main _sub]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (ui-segment {}
          (dom/div "Request")
          (dom/div {} (str code))
          (dom/div {} (str "Query String: " query-string))
          (dom/div {} (u.links/ui-relay-link relay)))
        (u.menus/ui-nav-menu nav-menu)
        (ui-router router)))
    (u.debug/load-error props "requests show record")))

(def ui-show (comp/factory Show))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn [_props]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show)}])
   :route-segment ["request" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/label         "Show Requests"
   o.navlinks/input-key     model-key
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
