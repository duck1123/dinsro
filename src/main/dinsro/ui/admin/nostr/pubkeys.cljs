(ns dinsro.ui.admin.nostr.pubkeys
  (:require
   [com.fulcrologic.fulcro-css.css :as css]
   [com.fulcrologic.fulcro.components :as comp :refer [defsc]]
   [com.fulcrologic.fulcro.dom :as dom]
   [com.fulcrologic.fulcro.routing.dynamic-routing :as dr :refer [defrouter]]
   [com.fulcrologic.rad.report :as report]
   [com.fulcrologic.rad.report-options :as ro]
   [com.fulcrologic.rad.state-machines.server-paginated-report :as spr]
   [dinsro.joins.nostr.pubkeys :as j.n.pubkeys]
   [dinsro.model.navbars :as m.navbars]
   [dinsro.model.navlinks :as m.navlinks]
   [dinsro.model.nostr.pubkeys :as m.n.pubkeys]
   [dinsro.ui.admin.nostr.pubkeys.relays :as u.a.n.p.relays]
   [dinsro.ui.debug :as u.debug]
   [dinsro.ui.links :as u.links]
   [dinsro.ui.loader :as u.loader]
   [dinsro.ui.menus :as u.menus]
   [dinsro.ui.nostr.pubkeys :as u.n.pubkeys]

   [lambdaisland.glogc :as log]))

;; [[../../../joins/nostr/pubkeys.cljc]]
;; [[../../../model/nostr/pubkeys.cljc]]

(def index-page-key :admin-nostr-pubkeys)
(def model-key ::m.n.pubkeys/id)
(def show-menu-key :admin-nostr-pubkeys)
(def show-page-key :admin-nostr-pubkeys-show)

(report/defsc-report Report
  [_this _props]
  {ro/column-formatters {::m.n.pubkeys/hex     #(u.links/ui-pubkey-link %3)
                         ::m.n.pubkeys/name    #(u.links/ui-admin-pubkey-name-link %3)
                         ::m.n.pubkeys/picture #(u.links/img-formatter %3)}
   ro/columns           [m.n.pubkeys/picture
                         m.n.pubkeys/name
                         j.n.pubkeys/contact-count
                         j.n.pubkeys/event-count]
   ro/control-layout    {:action-buttons [::new ::refresh]}
   ro/controls          {::refresh u.links/refresh-control}
   ro/machine           spr/machine
   ro/page-size         10
   ro/paginate?         true
   ro/row-pk            m.n.pubkeys/id
   ro/run-on-mount?     true
   ro/source-attribute  ::j.n.pubkeys/index
   ro/title             "Pubkeys"})

(def ui-report (comp/factory Report))

(defrouter Router
  [_this _props]
  {:router-targets
   [u.a.n.p.relays/SubPage
    ;; u.a.n.r.connections/SubPage
    ;; u.a.n.r.pubkeys/SubPage
    ;; u.a.n.r.requests/SubPage
    ;; u.a.n.r.runs/SubPage
    ;; u.a.n.r.events/SubPage
    ;; u.a.n.r.witnesses/SubPage
    ]})
(def ui-router (comp/factory Router))

(defsc Show
  "Show a core node"
  [_this {::m.n.pubkeys/keys [id]
          :ui/keys           [nav-menu router]
          :as                props}]
  {:ident         ::m.n.pubkeys/id
   :initial-state (fn [props]
                    (log/info :Show/initial-state {:props props})
                    (let [id (get props model-key)]
                      {::m.n.pubkeys/about        ""
                       ::m.n.pubkeys/display-name ""
                       ::m.n.pubkeys/hex          ""
                       ::m.n.pubkeys/id           id
                       ::m.n.pubkeys/lud06        ""
                       ::m.n.pubkeys/name         ""
                       ::m.n.pubkeys/nip05        ""
                       ::j.n.pubkeys/npub         ""
                       ::m.n.pubkeys/picture      ""
                       ::m.n.pubkeys/website      ""
                       :ui/nav-menu (comp/get-initial-state u.menus/NavMenu {::m.navbars/id show-menu-key :id id})
                       :ui/router (comp/get-initial-state Router {})}))
   :pre-merge     (u.loader/page-merger model-key
                    {:ui/nav-menu [u.menus/NavMenu {::m.navbars/id show-menu-key}]
                     :ui/router   [Router {}]})
   :query         [::m.n.pubkeys/about
                   ::m.n.pubkeys/display-name
                   ::m.n.pubkeys/hex
                   ::m.n.pubkeys/id
                   ::m.n.pubkeys/lud06
                   ::m.n.pubkeys/name
                   ::m.n.pubkeys/nip05
                   ::j.n.pubkeys/npub
                   ::m.n.pubkeys/picture
                   ::m.n.pubkeys/website
                   {:ui/nav-menu (comp/get-query u.menus/NavMenu)}
                   {:ui/router (comp/get-query Router)}]}
  (log/debug :Show/starting {:props props})
  (let [{:keys [main]} (css/get-classnames Show)]
    (if id
      (dom/div {:classes [main]}
        #_(ui-segment {}
            (dom/div {} "TODO: Admin pubkey")
            (u.debug/log-props props))
        (u.n.pubkeys/ui-pubkey-info props)
        (u.menus/ui-nav-menu nav-menu)
        (ui-router router))
      (u.debug/load-error props "admin pubkey record"))))

(def ui-show (comp/factory Show))

(defsc IndexPage
  [_this {:ui/keys [report]
          :as props}]
  {:componentDidMount #(report/start-report! % Report {})
   :ident             (fn [] [::m.navlinks/id index-page-key])
   :initial-state     {::m.navlinks/id index-page-key
                       :ui/report      {}}
   :query             [::m.navlinks/id
                       {:ui/report (comp/get-query Report)}]
   :route-segment     ["pubkeys"]
   :will-enter        (u.loader/page-loader index-page-key)}
  (log/info :IndexPage/starting {:props props})
  (dom/div {}
    (ui-report report)))

(defsc ShowPage
  [_this {id                model-key
          ::m.navlinks/keys [target]
          :as               props}]
  {:ident         (fn [] [::m.navlinks/id show-page-key])
   :initial-state (fn [_props]
                    {model-key           nil
                     ::m.navlinks/id     show-page-key
                     ::m.navlinks/target (comp/get-initial-state Show {})})
   :query         (fn [_props]
                    [model-key
                     ::m.navlinks/id
                     {::m.navlinks/target (comp/get-query Show {})}])
   :route-segment ["pubkey" :id]
   :will-enter    (u.loader/targeted-router-loader show-page-key model-key ::ShowPage)}
  (log/info :ShowPage/starting {:props props})
  (if (and target id)
    (ui-show target)
    (u.debug/load-error props "admin show pubkey page")))
