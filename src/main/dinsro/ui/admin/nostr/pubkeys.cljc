(ns dinsro.ui.admin.nostr.pubkeys
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   #?(:cljs [com.fulcrologic.fulcro.dom :as dom])
   #?(:clj [com.fulcrologic.fulcro.dom-server :as dom])
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.mutations.nostr.pubkeys :as mu.n.pubkeys]
   [dinsro.options.navbars :as o.navbars]
   [dinsro.options.navlinks :as o.navlinks]
   [dinsro.options.nostr.pubkeys :as o.n.pubkeys]
   [dinsro.ui.admin.nostr.pubkeys.badge-acceptances :as u.a.n.p.badge-acceptances]
   [dinsro.ui.admin.nostr.pubkeys.badge-awards :as u.a.n.p.badge-awards]
   [dinsro.ui.admin.nostr.pubkeys.badge-definitions :as u.a.n.p.badge-definitions]
   [dinsro.ui.admin.nostr.pubkeys.contacts :as u.a.n.p.contacts]
   [dinsro.ui.admin.nostr.pubkeys.events :as u.a.n.p.events]
   [dinsro.ui.admin.nostr.pubkeys.items :as u.a.n.p.items]
   [dinsro.ui.admin.nostr.pubkeys.relays :as u.a.n.p.relays]
   [dinsro.ui.admin.nostr.pubkeys.users :as u.a.n.p.users]
   [dinsro.ui.buttons :as u.buttons]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]
   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/pubkeys.cljc]]
;; [[../../../model/nostr/pubkeys.cljc]]
;; [[../../../mutations/nostr/pubkeys.cljc]]
;; [[../../../processors/nostr/pubkeys.clj]]

(def index-page-id :admin-nostr-pubkeys)
(def model-key o.n.pubkeys/id)
(def parent-router-id :admin-nostr)
(def required-role :admin)
(def show-page-id :admin-nostr-pubkeys-show)

(def delete-action
  (u.buttons/row-action-button "Delete" model-key mu.n.pubkeys/delete!))

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {o.n.pubkeys/hex     #(u.links/ui-admin-pubkey-link %3)
                         o.n.pubkeys/name    #(u.links/ui-admin-pubkey-name-link %3)
                         o.n.pubkeys/picture #(u.links/img-formatter %3)}
   ro/columns           [m.n.pubkeys/picture
                         m.n.pubkeys/name
                         j.n.pubkeys/contact-count
                         j.n.pubkeys/event-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-actions       [delete-action]
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.pubkeys/admin-index
   ro/title             "Pubkeys"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.p.badge-acceptances/SubPage
    u.a.n.p.badge-awards/SubPage
    u.a.n.p.badge-definitions/SubPage
    u.a.n.p.items/SubPage
    u.a.n.p.relays/SubPage
    u.a.n.p.contacts/SubPage
    u.a.n.p.events/SubPage
    u.a.n.p.users/SubPage]})

(def ui-router (comp/factory Router))

(m.navbars/defmenu show-page-id
  {::m.navbars/parent   parent-router-id
   ::m.navbars/router   ::Router
   ::m.navbars/children
   [u.a.n.p.relays/index-page-id
    u.a.n.p.badge-acceptances/index-page-id
    u.a.n.p.badge-awards/index-page-id
    u.a.n.p.badge-definitions/index-page-id
    u.a.n.p.items/index-page-id
    u.a.n.p.contacts/index-page-id
    u.a.n.p.events/index-page-id
    u.a.n.p.users/index-page-id]})

(defsc Show
  [_this {:ui/keys [admin-nav-menu admin-router]
          :as      props}]
  {:ident         ::m.n.pubkeys/id
   :initial-state (fn [props]
                    (let [id (model-key props)]
                      {model-key                id
                       o.n.pubkeys/about        ""
                       o.n.pubkeys/display-name ""
                       o.n.pubkeys/hex          ""
                       o.n.pubkeys/id           nil
                       o.n.pubkeys/lud06        ""
                       o.n.pubkeys/name         ""
                       o.n.pubkeys/nip05        ""
                       ::j.n.pubkeys/npub       ""
                       o.n.pubkeys/picture      ""
                       o.n.pubkeys/website      ""
                       :ui/admin-nav-menu       (comp/get-initial-state u.menus/NavMenu
                                                  {o.navbars/id show-page-id
                                                   :id          id})
                       :ui/admin-router         (comp/get-initial-state Router {})}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/admin-nav-menu [u.menus/NavMenu {o.navbars/id show-page-id}]
                     :ui/admin-router   [Router {}]})
   :query         (fn []
                    [model-key
                     o.n.pubkeys/about
                     o.n.pubkeys/display-name
                     o.n.pubkeys/hex
                     o.n.pubkeys/lud06
                     o.n.pubkeys/name
                     o.n.pubkeys/nip05
                     ::j.n.pubkeys/npub
                     o.n.pubkeys/picture
                     o.n.pubkeys/website
                     {:ui/admin-nav-menu (comp/get-query u.menus/NavMenu)}
                     {:ui/admin-router (comp/get-query Router)}])}
  (log/debug :Show/starting {:props props})
  (if (model-key props)
    (let [{:keys [main]} (css/get-classnames Show)]
      (dom/div {:classes [main]}
        (u.n.pubkeys/ui-pubkey-info props)
        (u.menus/ui-nav-menu admin-nav-menu)
        (ui-router admin-router)))
    (u.debug/load-error props "admin show pubkey record")))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as      props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [o.navlinks/id index-page-id])
   :initial-state     (fn [_props]
                        {o.navlinks/id index-page-id
                         :ui/report    (comp/get-initial-state Report {})})
   :query             (fn []
                        [o.navlinks/id
                         {:ui/report (comp/get-query Report)}])
   :route-segment     ["pubkeys"]
   :will-enter        (u.loader/page-loader index-page-id)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this props]
  {:ident         (fn [] [o.navlinks/id show-page-id])
   :initial-state (fn [props]
                    {model-key         (model-key props)
                     o.navlinks/id     show-page-id
                     o.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn []
                    [model-key
                     o.navlinks/id
                     {o.navlinks/target (comp/get-query Show {})}])
   :route-segment ["pubkey" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-id model-key ::ShowPage)}
  (u.loader/show-page props model-key ui-show))

(m.navlinks/defroute index-page-id
  {o.navlinks/control       ::IndexPage
   o.navlinks/label         "Pubkeys"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    parent-router-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})

(m.navlinks/defroute show-page-id
  {o.navlinks/control       ::ShowPage
   o.navlinks/input-key     model-key
   o.navlinks/label         "Show Pubkey"
   o.navlinks/model-key     model-key
   o.navlinks/parent-key    index-page-id
   o.navlinks/navigate-key  u.a.n.p.relays/index-page-id
   o.navlinks/router        parent-router-id
   o.navlinks/required-role required-role})
